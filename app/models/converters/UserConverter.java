package models.converters;

import models.domain.User;
import models.dto.UserDto;
import security.PasswordHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserConverter {
    public User convertDtoToUser(UserDto userDto) throws ParseException {

        User user = new User();
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
}
