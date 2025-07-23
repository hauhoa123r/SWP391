package org.project.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging data to the console
 */
public class LogUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Log any object to the console with INFO level
     * 
     * @param data Object to log
     */
    public static void logData(Object data) {
        try {
            if (data == null) {
                logger.info("Data is null");
                return;
            }
            
            if (data instanceof String) {
                logger.info("Data: {}", data);
            } else {
                String jsonData = objectMapper.writeValueAsString(data);
                logger.info("Data: {}", jsonData);
            }
        } catch (Exception e) {
            logger.error("Error logging data: {}", e.getMessage());
            logger.info("Data toString(): {}", data.toString());
        }
    }
    
    /**
     * Log any object to the console with custom message and INFO level
     * 
     * @param message Custom message
     * @param data Object to log
     */
    public static void logData(String message, Object data) {
        try {
            if (data == null) {
                logger.info("{}: null", message);
                return;
            }
            
            if (data instanceof String) {
                logger.info("{}: {}", message, data);
            } else {
                String jsonData = objectMapper.writeValueAsString(data);
                logger.info("{}: {}", message, jsonData);
            }
        } catch (Exception e) {
            logger.error("Error logging data: {}", e.getMessage());
            logger.info("{}: {}", message, data.toString());
        }
    }
    
    /**
     * Log any object to the console with DEBUG level
     * 
     * @param data Object to log
     */
    public static void logDebug(Object data) {
        try {
            if (data == null) {
                logger.debug("Data is null");
                return;
            }
            
            if (data instanceof String) {
                logger.debug("Data: {}", data);
            } else {
                String jsonData = objectMapper.writeValueAsString(data);
                logger.debug("Data: {}", jsonData);
            }
        } catch (Exception e) {
            logger.error("Error logging data: {}", e.getMessage());
            logger.debug("Data toString(): {}", data.toString());
        }
    }
    
    /**
     * Log repository query execution for debugging purposes
     * 
     * @param repositoryName Name of the repository
     * @param methodName Name of the repository method
     * @param params Parameters passed to repository method
     * @param result Result of repository method
     */
    public static void logRepositoryQuery(String repositoryName, String methodName, Object params, Object result) {
        try {
            StringBuilder message = new StringBuilder();
            message.append("Repository: ").append(repositoryName);
            message.append(", Method: ").append(methodName);
            
            if (params != null) {
                String paramsStr = params instanceof String ? 
                    (String) params : objectMapper.writeValueAsString(params);
                message.append(", Params: ").append(paramsStr);
            }
            
            logger.debug(message.toString());
            
            if (result != null) {
                String resultStr = result instanceof String ? 
                    (String) result : objectMapper.writeValueAsString(result);
                logger.debug("Result: {}", resultStr);
            } else {
                logger.debug("Result: null");
            }
            
        } catch (Exception e) {
            logger.error("Error logging repository query: {}", e.getMessage());
        }
    }
    
    /**
     * Log service operation for debugging purposes
     * 
     * @param serviceName Name of the service
     * @param methodName Name of the service method
     * @param input Input parameters to service method
     * @param output Output of service method
     */
    public static void logServiceOperation(String serviceName, String methodName, Object input, Object output) {
        try {
            StringBuilder message = new StringBuilder();
            message.append("Service: ").append(serviceName);
            message.append(", Method: ").append(methodName);
            
            if (input != null) {
                String inputStr = input instanceof String ? 
                    (String) input : objectMapper.writeValueAsString(input);
                logger.info(message.toString());
                logger.info("Input: {}", inputStr);
            } else {
                logger.info(message.toString() + ", Input: null");
            }
            
            if (output != null) {
                String outputStr = output instanceof String ? 
                    (String) output : objectMapper.writeValueAsString(output);
                logger.info("Output: {}", outputStr);
            } else {
                logger.info("Output: null");
            }
            
        } catch (Exception e) {
            logger.error("Error logging service operation: {}", e.getMessage());
        }
    }
} 