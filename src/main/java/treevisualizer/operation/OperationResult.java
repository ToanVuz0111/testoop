package treevisualizer.operation;

import java.util.ArrayList;
import java.util.List;

public class OperationResult {
    private List<OperationStep> steps;
    private String operationName;
    private String pseudocodeKey;
    private boolean success;
    private String errorMessage;

    public OperationResult(String operationName, String pseudocodeKey) {
        this.operationName = operationName;
        this.pseudocodeKey = pseudocodeKey;
        this.steps = new ArrayList<>();
        this.success = true;
    }

    public void addStep(OperationStep step) {
        steps.add(step);
    }

    public List<OperationStep> getSteps() { return steps; }
    public String getOperationName() { return operationName; }
    public String getPseudocodeKey() { return pseudocodeKey; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
