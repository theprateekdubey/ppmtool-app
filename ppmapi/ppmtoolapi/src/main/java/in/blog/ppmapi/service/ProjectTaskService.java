package in.blog.ppmapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.blog.ppmapi.domain.Backlog;
import in.blog.ppmapi.domain.Project;
import in.blog.ppmapi.domain.ProjectTask;
import in.blog.ppmapi.exception.ProjectNotFoundException;
import in.blog.ppmapi.repository.BacklogRepository;
import in.blog.ppmapi.repository.ProjectRepository;
import in.blog.ppmapi.repository.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	@Autowired
	private ProjectRepository projectRepository;

	public ProjectTask addProjectTask(String projectIdentifier , ProjectTask projectTask) {
		try {
			//Exception handling: in case project is not available
			//ProjectTAsk should be added to a specific Project , project != null, backlog exist
			Backlog backlog =backlogRepository.findByProjectIdentifier(projectIdentifier);			
			//set the backlog to the project Task
			projectTask.setBacklog(backlog);
			//we want our project to look like : IDPRO-1, IDPRO-2...
			Integer backlogSequence =backlog.getPTSequence();
			//Update backlog sequence
			backlogSequence++;
			backlog.setPTSequence(backlogSequence);
			// Add Backlog sequence to ProjectTask
			projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);//FP01-1,FP01-2,SP01-1
			projectTask.setProjectIdentifer(projectIdentifier);
			//setting default priority and status
			if(projectTask.getPriority()==0 || projectTask.getPriority()==null) {
				projectTask.setPriority(3);	//Low Priority
			}
			if(projectTask.getStatus()=="" || projectTask.getStatus()==null) {
				projectTask.setStatus("TODO");
			}
			// Few changes done by me
			ProjectTask projectTask2 = projectTaskRepository.save(projectTask);
			backlogRepository.save(backlog);
			return projectTask2;
		} catch (Exception ex) {
			throw new ProjectNotFoundException("Project not found");
		}
	}
	public Iterable<ProjectTask> findBacklogById(String id){//FP02
		Project project=projectRepository.findByProjectIdentifier(id);
		if (project==null) {
			throw new ProjectNotFoundException("Project Not Found");
		}
		return projectTaskRepository.findProjectIdentifierOrderByPriority(id);
	}
	public ProjectTask findPTByProjectSequence(String backlog_id,String pt_id) {
		//make sure we are searching on an existing backlog
		Backlog backlog=backlogRepository.findByProjectIdentifier(backlog_id);
		if (backlog==null) {
			throw new ProjectNotFoundException("Project with id:'"+pt_id+"'does not exist");
		}
		//make sure task exists
		ProjectTask projectTask=projectTaskRepository.findByProjectSequence(pt_id);
		if (projectTask==null) {
			throw new ProjectNotFoundException("Project task:'"+pt_id+"'does not exist");
		}
		//make sure that the backlog/project_id in the path corresponding to the right project
		if (!projectTask.getProjectIdentifer().equals(backlog_id)) {
			throw new ProjectNotFoundException("Backlog id:'"+backlog_id+"'does not match with project identifier: '"+projectTask.getProjectIdentifer()+"'");
		}
		return projectTask;
	}
	
	public ProjectTask updateByProjectSequence(ProjectTask updateTask,String backlog_id,String pt_id) {
		//find existing project task
		ProjectTask projectTask=findPTByProjectSequence(backlog_id, pt_id);
		//Replace with updated task
		projectTask=updateTask;
		//save projectTask
		return projectTaskRepository.save(projectTask);
	}
	public void deletePTByProjectSequence(String backlog_id,String pt_id) {
		//find existing project task
		ProjectTask projectTask=findPTByProjectSequence(backlog_id, pt_id);
		Backlog backlog=projectTask.getBacklog();
		List<ProjectTask> pts=backlog.getProjectTasks();
		pts.remove(projectTask);
		backlogRepository.save(backlog);
		projectTaskRepository.delete(projectTask);
	}
	
		
}