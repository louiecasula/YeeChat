package com.example.demo.services;

import com.example.demo.dtos.WorkspaceDto;
import com.example.demo.models.Channel;
import com.example.demo.models.User;
import com.example.demo.models.Workspace;
import com.example.demo.repositories.ChannelRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.WorkspaceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public WorkspaceService(WorkspaceRepository workspaceRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public Workspace createWorkspace(WorkspaceDto workspaceDto) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.setWorkspaceName(workspaceDto.getWorkspaceName());
        newWorkspace.setAccessible(workspaceDto.getAccessible());
        newWorkspace.setVisible(workspaceDto.getVisible());
        return workspaceRepository.save(newWorkspace);
    }

    public Workspace getWorkspaceById(Long workspaceId) throws Exception {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
    }

    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    public Workspace updateWorkspace(Long workspaceId, WorkspaceDto workspaceDto) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        workspace.setWorkspaceName(workspaceDto.getWorkspaceName());
        workspace.setAccessible(workspaceDto.getAccessible());
        workspace.setVisible(workspaceDto.getVisible());
        return workspaceRepository.save(workspace);
    }

    public void deleteWorkspace(Long workspaceId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        workspaceRepository.delete(workspace);
    }

    public User addUserToWorkspace(Long workspaceId, Long userId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User doesn't exist"));
        workspace.getUsers().add(user);
        user.getWorkspaces().add(workspace);
        workspaceRepository.save(workspace);
        return userRepository.save(user);
    }

    public void removeUserFromWorkspace(Long workspaceId, Long userId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User doesn't exist"));
        workspace.getUsers().remove(user);
        user.getWorkspaces().remove(workspace);
        workspaceRepository.save(workspace);
        userRepository.save(user);
    }

    public Set<User> getAllUsersInWorkspace(Long workspaceId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        return workspace.getUsers();
    }

    public Set<Channel> getAllChannelsInWorkspace(Long workspaceId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        return workspace.getChannels();
    }

    public Workspace addChannelToWorkspace(Long workspaceId, Long channelId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new Exception("Workspace doesn't exist"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new Exception("Channel doesn't exist"));
        workspace.getChannels().add(channel);
        workspaceRepository.save(workspace);
        return workspace;
    }

    public Set<Workspace> getWorkspacesForUser(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User doesn't exist"));
        return user.getWorkspaces();
    }

    @Transactional
    public Workspace createWorkspaceByUser(Long userId, WorkspaceDto workspaceDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User doesn't exist"));

        Workspace newWorkspace = new Workspace();
        newWorkspace.setWorkspaceName(workspaceDto.getWorkspaceName());
        newWorkspace.setAccessible(workspaceDto.getAccessible() != null ? workspaceDto.getAccessible() : true);
        newWorkspace.setVisible(workspaceDto.getVisible() != null ? workspaceDto.getVisible() : true);
        newWorkspace.setAdmin(user);
        user.getWorkspaces().add(newWorkspace);
        workspaceRepository.save(newWorkspace);

        Channel general = new Channel(workspaceDto.getWorkspaceName() + " General", true, true);
        general.setWorkspace(newWorkspace);
        newWorkspace.getChannels().add(general);

        general.getUsers().add(user);

        channelRepository.save(general);

        userRepository.save(user);

        return newWorkspace;
    }
}
