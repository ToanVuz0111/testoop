package treevisualizer.operation;

import java.util.ArrayList;
import java.util.List;

public class OperationStep {
    private String description;
    private int highlightedCodeLine;
    private List<Integer> highlightedNodeValues;
    private String highlightColor;
    private String pseudocodeKey;

    public OperationStep(String description, int highlightedCodeLine,
                         List<Integer> highlightedNodeValues, String highlightColor,
                         String pseudocodeKey) {
        this.description = description;
        this.highlightedCodeLine = highlightedCodeLine;
        this.highlightedNodeValues = highlightedNodeValues != null ? highlightedNodeValues : new ArrayList<>();
        this.highlightColor = highlightColor;
        this.pseudocodeKey = pseudocodeKey;
    }

    public String getDescription() { return description; }
    public int getHighlightedCodeLine() { return highlightedCodeLine; }
    public List<Integer> getHighlightedNodeValues() { return highlightedNodeValues; }
    public String getHighlightColor() { return highlightColor; }
    public String getPseudocodeKey() { return pseudocodeKey; }
}
