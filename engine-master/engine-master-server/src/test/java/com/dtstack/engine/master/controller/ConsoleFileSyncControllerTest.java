package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.dto.ConsoleFileSyncDirectoryDTO;
import com.dtstack.engine.master.impl.ConsoleFileSyncService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@MockWith(ConsoleFileSyncControllerTest.Mock.class)
public class ConsoleFileSyncControllerTest {

    private ConsoleFileSyncController controller = new ConsoleFileSyncController();

    public static class Mock {

        @MockInvoke(targetClass = ConsoleFileSyncService.class)
        public List<ConsoleFileSyncDirectoryDTO> allDirectories() {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ConsoleFileSyncService.class)
        public ConsoleFileSyncDirectoryDTO allFilesByDirectory(String directory) {
            return new ConsoleFileSyncDirectoryDTO();
        }

        @MockInvoke(targetClass = ConsoleFileSyncService.class)
        public void save(ConsoleFileSyncDirectoryDTO directoryDTO) {

        }

        @MockInvoke(targetClass = ConsoleFileSyncService.class)
        public ConsoleFileSyncDirectoryDTO load(Long clusterId) {
            return new ConsoleFileSyncDirectoryDTO();
        }
        }

    @Test
    public void allDirectories() {
        controller.allDirectories();
    }

    @Test
    public void allFilesByDirectory() {
        controller.allFilesByDirectory("");
    }

    @Test
    public void save() {
        controller.save(new ConsoleFileSyncDirectoryDTO());
    }

    @Test
    public void load() {
        controller.load(1L);
    }
}