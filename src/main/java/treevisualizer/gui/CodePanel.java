package treevisualizer.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class CodePanel extends JPanel {
    private JList<String> codeList;
    private DefaultListModel<String> listModel;
    private int highlightedLine = -1;
    private String currentKey = "";

    private static final Map<String, String[]> PSEUDOCODE = new HashMap<>();

    static {
        PSEUDOCODE.put("CREATE_GENERIC", new String[]{
            "procedure create():",
            "  tree ← empty",
            "  return tree"
        });
        PSEUDOCODE.put("INSERT_GENERIC", new String[]{
            "procedure insert(parent, value):",
            "  node ← find(root, parent)",
            "  if node = null then:",
            "    return error",
            "  newNode ← createNode(value)",
            "  node.children.add(newNode)",
            "  return success"
        });
        PSEUDOCODE.put("DELETE_GENERIC", new String[]{
            "procedure delete(value):",
            "  node ← find(root, value)",
            "  if node = null then:",
            "    return error",
            "  parent.children.remove(node)",
            "  return success"
        });
        PSEUDOCODE.put("UPDATE_GENERIC", new String[]{
            "procedure update(old, new):",
            "  node ← find(root, old)",
            "  if node = null then return error",
            "  node.value ← new",
            "  return success"
        });
        PSEUDOCODE.put("SEARCH_GENERIC", new String[]{
            "procedure search(value):",
            "  for each node in BFS order:",
            "    if node.value = value then:",
            "      return node",
            "  return null"
        });
        PSEUDOCODE.put("TRAVERSE_GENERIC", new String[]{
            "procedure traverse(root):",
            "  if root = null then return",
            "  visit(root)",
            "  enqueue children",
            "  for each child:",
            "    traverse(child)",
            "  return result"
        });
        PSEUDOCODE.put("CREATE_BINARY", new String[]{
            "procedure create():",
            "  root ← null",
            "  return tree"
        });
        PSEUDOCODE.put("INSERT_BINARY", new String[]{
            "procedure insert(value):",
            "  if root = null then:",
            "    root ← createNode(value)",
            "    return",
            "  curr ← root",
            "  while curr ≠ null:",
            "    if value < curr.value then:",
            "      if curr.left = null then:",
            "        curr.left ← createNode(value)",
            "        return",
            "      curr ← curr.left",
            "    else if value > curr.value then:",
            "      if curr.right = null then:",
            "        curr.right ← createNode(value)",
            "        return",
            "      curr ← curr.right",
            "    else:",
            "      return // duplicate"
        });
        PSEUDOCODE.put("DELETE_BINARY", new String[]{
            "procedure delete(value):",
            "  node ← bstSearch(root, value)",
            "  if node = null then:",
            "    return error",
            "  found node, start deletion",
            "  if leaf: remove directly",
            "  if one child: replace with child",
            "  if two children: find successor",
            "  replace value with successor",
            "  delete successor"
        });
        PSEUDOCODE.put("UPDATE_BINARY", new String[]{
            "procedure update(old, new):",
            "  delete(old)",
            "  insert(new)",
            "  return success"
        });
        PSEUDOCODE.put("SEARCH_BINARY", new String[]{
            "procedure search(value):",
            "  curr ← root",
            "  while curr ≠ null:",
            "    if value = curr.value then:",
            "      return curr",
            "    if value < curr.value then:",
            "      curr ← curr.left",
            "    else:",
            "      curr ← curr.right",
            "  return null"
        });
        PSEUDOCODE.put("TRAVERSE_BINARY", new String[]{
            "procedure traverse(root, mode):",
            "  preorder: visit, left, right",
            "  visit node",
            "  recurse left",
            "  inorder: left, visit, right",
            "  recurse right",
            "  postorder: left, right, visit",
            "  recurse",
            "  bfs: use queue",
            "  return result"
        });
        PSEUDOCODE.put("CREATE_RB", new String[]{
            "procedure createRBTree():",
            "  root ← null",
            "  return tree"
        });
        PSEUDOCODE.put("INSERT_RB", new String[]{
            "procedure rbInsert(value):",
            "  if root = null: root ← RED node",
            "  do standard BST insert",
            "  compare and traverse",
            "  handle duplicates",
            "  new node is RED",
            "  root.color ← BLACK",
            "  fixInsertViolations(node)",
            "  Case 1: uncle RED → recolor",
            "  parent.color ← BLACK",
            "  uncle.color ← BLACK",
            "  grandparent.color ← RED",
            "  Case 2: rotate",
            "  Case 3: rotate + recolor"
        });
        PSEUDOCODE.put("DELETE_RB", new String[]{
            "procedure rbDelete(value):",
            "  node ← bstSearch(root, value)",
            "  if node = null: return error",
            "  perform BST deletion",
            "  fixDeleteViolations()",
            "  root.color ← BLACK"
        });
    }

    public CodePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 35));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel title = new JLabel(" Pseudocode", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 13));
        title.setForeground(new Color(150, 200, 255));
        title.setBackground(new Color(35, 35, 50));
        title.setOpaque(true);
        title.setBorder(new EmptyBorder(6, 8, 6, 8));
        add(title, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        codeList = new JList<>(listModel) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
            }
        };
        codeList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        codeList.setBackground(new Color(25, 25, 35));
        codeList.setForeground(new Color(200, 200, 200));
        codeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        codeList.setCellRenderer(new CodeLineCellRenderer());
        codeList.setFixedCellHeight(22);

        JScrollPane scrollPane = new JScrollPane(codeList);
        scrollPane.setBackground(new Color(25, 25, 35));
        scrollPane.getViewport().setBackground(new Color(25, 25, 35));
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setOperation(String key, int highlightLine) {
        if (key == null) return;

        if (!key.equals(currentKey)) {
            currentKey = key;
            listModel.clear();
            String[] lines = PSEUDOCODE.getOrDefault(key, new String[]{"// No pseudocode available"});
            for (String line : lines) {
                listModel.addElement(line);
            }
        }

        highlightedLine = highlightLine;
        codeList.repaint();

        if (highlightLine >= 0 && highlightLine < listModel.size()) {
            codeList.ensureIndexIsVisible(highlightLine);
        }
    }

    private class CodeLineCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                       boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, false, false);
            label.setFont(new Font("Monospaced", Font.PLAIN, 12));
            label.setBorder(new EmptyBorder(2, 8, 2, 8));

            if (index == highlightedLine) {
                label.setBackground(new Color(200, 180, 0));
                label.setForeground(new Color(20, 20, 20));
                label.setFont(new Font("Monospaced", Font.BOLD, 12));
            } else {
                label.setBackground(new Color(25, 25, 35));
                label.setForeground(new Color(200, 200, 200));
            }
            return label;
        }
    }
}
