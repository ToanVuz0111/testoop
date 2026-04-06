package treevisualizer.model;

public abstract class TreeNode {
    protected int value;
    protected int x, y;
    protected boolean highlighted;
    protected String highlightColor;

    public TreeNode(int value) {
        this.value = value;
        this.highlighted = false;
        this.highlightColor = "yellow";
    }

    public abstract TreeNode[] getChildren();

    public int getValue() { return value; }
    public void setValue(int v) { value = v; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean h) { highlighted = h; }
    public String getHighlightColor() { return highlightColor; }
    public void setHighlightColor(String c) { highlightColor = c; }
}
