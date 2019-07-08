package com.example.money;

import com.example.money.data.NBPPayloadFactory;
import com.example.money.model.configuration.CountryConfiguration;
import com.example.money.model.form.Country;
import com.example.money.model.form.CountryWrapper;
import com.example.money.model.network.NBPPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {MoneyApplication.class, MoneyApplicationTests.TestConfiguration.class})
@TestPropertySource(properties = "nbp.url=http://api.nbp.pl/api/exchangerates/rates/a/%s/")
public class MoneyApplicationTests {

    @Configuration
    static class TestConfiguration {

        @Value("${nbp.url}")
        private String url;

        //use fake rest template for mock nbp server response
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            RestTemplateBuilder restTemplateBuilder = Mockito.mock(RestTemplateBuilder.class);
            RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

            Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
            Mockito.when(restTemplate.getForObject(String.format(url, Mockito.anyString()), Mockito.eq(NBPPayload.class)))
                    .thenReturn(NBPPayloadFactory.createNBPPayload());
            return restTemplateBuilder;
        }
    }

    private static final BigDecimal DEFAULT_GROSS_PRICE = BigDecimal.valueOf(100.5);

    private static final int DAYS = 22;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private List<CountryConfiguration> configurations;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void shouldReturnPreparedCountriesPage() throws Exception {
		//when
		var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/app/money")).andReturn();

		//then
		assert mvcResult.getModelAndView() != null && mvcResult.getModelAndView().getModel() != null;
		var modelAndView = mvcResult.getModelAndView();
		var model = modelAndView.getModel();

		Assert.assertEquals(modelAndView.getViewName(), "countries");

		Assert.assertFalse(model.isEmpty());
		Assert.assertTrue(model.containsKey("wrapper"));
		assert model.get("wrapper") instanceof CountryWrapper;

		var countries = ((CountryWrapper) model.get("wrapper")).getCountries();
		Assert.assertEquals(3, countries.size());

		assert countries.size() == configurations.size();
		Assert.assertTrue(countries.stream()
				.map(Country::getConfiguration)
				.collect(Collectors.toList()).containsAll(configurations.stream()
					.map(CountryConfiguration::getConfiguration)
					.collect(Collectors.toList()))
		);
	}

	@Test
	public void shouldReturnCorrectNetPrice() throws Exception {
		//given
		//mock country wrapper payload
        var mvcResponse = mockMvc.perform(MockMvcRequestBuilders.get("/app/money")).andReturn();
        var countryWrapper = (CountryWrapper) Objects.requireNonNull(mvcResponse.getModelAndView()).getModel().get("wrapper");
        countryWrapper.getCountries().forEach(country ->
            country.setGrossPrice(DEFAULT_GROSS_PRICE)
        );
        //when
        var mvcResult = mockMvc.perform(post("/app/money/result")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(toFormParams(countryWrapper)))
                .andReturn();
        //then
        var model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();

        assert model != null && model.containsKey("wrapper") && model.get("wrapper") instanceof CountryWrapper;
        ((CountryWrapper) model.get("wrapper")).getCountries().forEach(country -> {
            var config = configurations.stream()
                    .filter(countryConfiguration -> countryConfiguration.getName().equals(country.getName()))
                    .findAny()
                    .orElse(null);

            assert config != null;
            var salary = country.getGrossPrice().multiply(BigDecimal.valueOf(DAYS)).subtract(BigDecimal.valueOf(config.getCost()));
            var tax = salary.multiply(BigDecimal.valueOf(config.getTax() / 100));

            var netPriceInPLN = salary.subtract(tax);
            if (!country.getLabel().equals("PLN")) {
                netPriceInPLN = netPriceInPLN.multiply(BigDecimal.valueOf(NBPPayloadFactory.DEFAULT_RATE));
            }
            Assert.assertEquals(netPriceInPLN, country.getNetPrice());
        });
    }

    private MultiValueMap<String, String> toFormParams(CountryWrapper countryWrapper) throws Exception {
        String template = "%s[%d].%s";
        ObjectReader reader = objectMapper.readerFor(Map.class);
        Map<String, List<Map<String, Object>>> map = reader.readValue(objectMapper.writeValueAsString(countryWrapper));

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        int index = 0;
        for (Map.Entry<String, List<Map<String, Object>>> entry : map.entrySet()) {
            String key = entry.getKey();
            for (Map<String, Object> list : entry.getValue()) {
                for (Map.Entry<String, Object> e : list.entrySet()) {
                    multiValueMap.add(String.format(template, key, index, e.getKey()),
                            Objects.isNull(e.getValue()) ? "" : e.getValue().toString());
                }
                index = index + 1;
            }

        }
        return multiValueMap;
    }
}
