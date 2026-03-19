package com.salesmanager.test.business.loader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.model.system.IntegrationModule;

/**
 * Test case demonstrating improved testable design
 */
public class IntegrationModulesLoaderTest {

    static class StubIntegrationModuleSource implements IntegrationModulesLoader.IntegrationModuleSource {
        private String jsonToReturn;
        private boolean throwException;
        private String exceptionMessage;

        public StubIntegrationModuleSource(String jsonToReturn) {
            this.jsonToReturn = jsonToReturn;
            this.throwException = false;
            this.exceptionMessage = "Stubbed exception: file not found";
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        public void setExceptionMessage(String msg) {
            this.exceptionMessage = msg;
        }

        @Override
        public String loadRawJson(String identifier) throws ServiceException {
            if (throwException) {
                throw new ServiceException(exceptionMessage);
            }
            return jsonToReturn;
        }
    }

    @Test
    public void testLoadModulesWithValidJson() throws Exception {
        String validJson = "[{\"module\": \"payment\", \"code\": \"paypal\", \"image\": \"paypal.png\"}]";

        IntegrationModulesLoader.IntegrationModuleSource stubSource = new StubIntegrationModuleSource(validJson);
        IntegrationModulesLoader.IntegrationModuleParser mockParser = mock(
                IntegrationModulesLoader.IntegrationModuleParser.class);

        List<IntegrationModule> expectedModules = new ArrayList<>();
        IntegrationModule module = new IntegrationModule();
        module.setCode("paypal");
        expectedModules.add(module);

        when(mockParser.parseModules(validJson)).thenReturn(expectedModules);

        IntegrationModulesLoader loader = new IntegrationModulesLoader(stubSource, mockParser);
        List<IntegrationModule> result = loader.loadIntegrationModules("data/modules.json");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("paypal", result.get(0).getCode());
        verify(mockParser).parseModules(validJson);
    }

    @Test
    public void testLoadModulesWithSourceException() throws Exception {
        StubIntegrationModuleSource stubSource = new StubIntegrationModuleSource("");
        stubSource.setThrowException(true);
        stubSource.setExceptionMessage("file not found");

        IntegrationModulesLoader.IntegrationModuleParser mockParser = mock(
                IntegrationModulesLoader.IntegrationModuleParser.class);
        IntegrationModulesLoader loader = new IntegrationModulesLoader(stubSource, mockParser);

        assertThrows(ServiceException.class, () -> {
            loader.loadIntegrationModules("nonexistent.json");
        });
    }

    @Test
    public void testLoaderWithDifferentSources() throws Exception {
        IntegrationModulesLoader.IntegrationModuleSource source1 = new StubIntegrationModuleSource(
                "[{\"module\": \"p\", \"code\": \"s\", \"image\": \"i\"}]");

        IntegrationModulesLoader.IntegrationModuleParser mockParser = mock(
                IntegrationModulesLoader.IntegrationModuleParser.class);
        when(mockParser.parseModules(anyString())).thenReturn(new ArrayList<>());

        IntegrationModulesLoader loader1 = new IntegrationModulesLoader(source1, mockParser);
        assertNotNull(loader1.loadIntegrationModules("modules.json"));
    }
}
