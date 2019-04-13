package models.converters;

import models.domain.Student;
import models.domain.User;
import models.dto.StudentDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentConverter {
    public Student convertDtoToStudent(StudentDto studentDto) throws ParseException {

        Student student = new Student();
        student.setUuid(studentDto.getUuid());
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        Date parsed;
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd");
        parsed = format.parse(studentDto.getDateOfBirth());

        student.setDateOfBirth(parsed);
        student.setEmail(studentDto.getEmail());
        student.setInstitute(studentDto.getInstitute());

        return student;
    }
}
