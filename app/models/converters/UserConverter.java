package models.converters;

import com.fasterxml.jackson.databind.JsonNode;
import models.api.ApiError;
import models.domain.User;
import models.dto.UserDto;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static play.libs.Json.toJson;

public class UserConverter {
    public User convertDtoToUser(UserDto userDto) throws ParseException {

        User user = new User();
        user.setUuid(userDto.getUuid());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        Date parsed;
        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd");
            parsed = format.parse(userDto.getDateOfBirth());
        }
        catch(ParseException pe) {
            throw pe;
        }
        user.setDateOfBirth(parsed);
        user.setEmail(userDto.getEmail());
        user.setInstitute(userDto.getInstitute());

        return user;
    }

    public UserDto convertFormToUser(FormFactory formFactory, Http.Request request){
        return null;
    }
}
