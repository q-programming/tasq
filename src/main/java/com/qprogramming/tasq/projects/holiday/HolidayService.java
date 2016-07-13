package com.qprogramming.tasq.projects.holiday;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jromaniszyn on 06.07.2016.
 */
@Service
public class HolidayService {
    private HolidayRepository holidayRepository;


    @Autowired
    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    /**
     * Remove holiday
     *
     * @param holiday
     */
    public void delete(Holiday holiday) {
        holidayRepository.delete(holiday);
    }

    /**
     * Create or update holiday
     *
     * @param holiday
     * @return
     */
    public Holiday save(Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    /**
     * Check if holidays were either removed or add in passed project. If passed holidaysSet is empty, all holidays will be removed
     * Have to be executed with full project object ( holidays filled in)
     *
     * @param holidaysSet - set containing holidays for project.
     * @param project     - project for which holidays are updated
     * @return Project with updated holidays
     */
    public Project processProjectHolidays(Set<String> holidaysSet, Project project) {
        Set<Holiday> projectHolidays = project.getHolidays();
        //delete removed holidays
        projectHolidays.stream()
                .filter(holiday -> !holidaysSet.contains(holiday.getStringDate()))
                .forEach(holiday -> {
                    project.getHolidays().remove(holiday);
                    delete(holiday);
                });
        //Add all new holidays
        if (!holidaysSet.isEmpty()) {
            Set<String> stringHolidays = project.getHolidays().stream().map(Holiday::getStringDate).collect(Collectors.toSet());
            Set<Holiday> newHolidaySet = holidaysSet.stream()
                    .filter(string -> !stringHolidays.contains(string))
                    .map(string -> new Holiday(Utils.convertStringToDate(string)))
                    .collect(Collectors.toSet());
            newHolidaySet.stream().forEach(holiday -> holiday = save(holiday));
            project.getHolidays().addAll(newHolidaySet);
        }
        return project;
    }

}
