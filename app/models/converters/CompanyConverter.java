package models.converters;

import models.domain.Company;
import models.dto.CompanyDto;

/**
 * @author Max Meijer
 * Created on 13/04/2019
 */
public class CompanyConverter {

    public Company convert(CompanyDto dto) throws NumberFormatException {
        Company company = new Company();
        company.setEmail(dto.getEmail());
        company.setCity(dto.getCity());
        company.setDescription(dto.getDescription());
        company.setHousenumber(Integer.parseInt(dto.getHouseNumber()));
        company.setName(dto.getName());
        company.setPostalcode(dto.getPostalCode());
        company.setStreetname(dto.getStreetName());

        return company;
    }

    public CompanyDto convert(Company company) {
        CompanyDto dto = new CompanyDto();

        dto.setUuid(company.getUuid());
        dto.setEmail(company.getEmail());
        dto.setCity(company.getCity());
        dto.setDescription(company.getDescription());
        dto.setHouseNumber(String.valueOf(company.getHousenumber()));
        dto.setName(company.getName());
        dto.setPostalCode(company.getPostalcode());
        dto.setStreetName(company.getStreetname());

        return dto;
    }
}
