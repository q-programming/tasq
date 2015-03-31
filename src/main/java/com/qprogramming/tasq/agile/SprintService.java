package com.qprogramming.tasq.agile;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SprintService {

	private SprintRepository sprintRepo;

	@Autowired
	public SprintService(SprintRepository sprintRepo) {
		this.sprintRepo = sprintRepo;
	}

	public Sprint findByProjectIdAndActiveTrue(Long id) {
		return sprintRepo.findByProjectIdAndActiveTrue(id);
	}

	public List<Sprint> findByProjectIdAndFinished(Long id, boolean finished) {
		return sprintRepo.findByProjectIdAndFinished(id, finished);
	}

	public List<Sprint> findByProjectId(Long id) {
		return sprintRepo.findByProjectId(id);
	}

	public Sprint findById(Long sprintID) {
		return sprintRepo.findById(sprintID);
	}

	public Sprint save(Sprint sprint) {
		return sprintRepo.save(sprint);
	}

	public void delete(Sprint sprint) {
		sprintRepo.delete(sprint);
	}

	public Sprint findByProjectIdAndSprintNo(Long id, Long sprintNo) {
		return sprintRepo.findByProjectIdAndSprintNo(id, sprintNo);
	}

	public List<DisplaySprint> convertToDisplay(List<Sprint> projectSprints) {
		List<DisplaySprint> result = new LinkedList<DisplaySprint>();
		for (Sprint sprint : projectSprints) {
			result.add(new DisplaySprint(sprint));
		}
		return result;
	}

}
