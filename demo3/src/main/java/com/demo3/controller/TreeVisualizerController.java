package com.demo3.controller;

import com.demo3.service.TreeOperationService;
import com.demo3.tree.OperationStep;
import com.demo3.tree.OperationTrace;
import com.demo3.tree.TreeType;
import com.demo3.util.DialogHelper;
import com.demo3.util.InputValidator;
import com.demo3.view.TreeCanvasView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class TreeVisualizerController {
    @FXML private VBox mainMenuView;
    @FXML private BorderPane visualizerView;
    @FXML private Button openGenericButton;
    @FXML private Button openBinaryButton;
    @FXML private Button openRedBlackButton;
    @FXML private Button helpMenuButton;
    @FXML private Button quitButton;
    @FXML private Button backToMenuButton;
    @FXML private Button helpVisualizerButton;
    @FXML private ComboBox<TreeType> treeSelector;
    @FXML private ComboBox<com.demo3.tree.TraversalType> traversalSelector;
    @FXML private TextField parentField;
    @FXML private TextField valueField;
    @FXML private TextField newValueField;
    @FXML private Label statusLabel;
    @FXML private Label stepLabel;
    @FXML private Label traceCounterLabel;
    @FXML private Label validationLabel;
    @FXML private Label legendLabel;
    @FXML private Button createButton;
    @FXML private Button insertButton;
    @FXML private Button deleteButton;
    @FXML private Button updateButton;
    @FXML private Button searchButton;
    @FXML private Button traverseButton;
    @FXML private Button undoButton;
    @FXML private Button redoButton;
    @FXML private Button playPauseButton;
    @FXML private Button stepBackButton;
    @FXML private Button stepForwardButton;
    @FXML private Slider speedSlider;
    @FXML private ProgressBar progressBar;
    @FXML private VBox codePanel;
    @FXML private TreeCanvasView treeCanvas;

    private final TreeOperationService operationService = new TreeOperationService();
    private int currentStepIndex;
    private Timeline playback;
    private Stage stage;

    @FXML
    public void initialize() {
        treeSelector.getItems().setAll(TreeType.values());
        traversalSelector.getItems().setAll(com.demo3.tree.TraversalType.values());
        traversalSelector.setValue(com.demo3.tree.TraversalType.DFS);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.6);
        speedSlider.setMinorTickCount(1);
        validationLabel.setWrapText(true);
        legendLabel.setWrapText(true);

        openGenericButton.setOnAction(event -> openTree(TreeType.GENERIC));
        openBinaryButton.setOnAction(event -> openTree(TreeType.BINARY_SEARCH));
        openRedBlackButton.setOnAction(event -> openTree(TreeType.RED_BLACK));
        helpMenuButton.setOnAction(event -> showHelp());
        helpVisualizerButton.setOnAction(event -> showHelp());
        quitButton.setOnAction(event -> {
            if (stage != null) {
                stage.close();
            }
        });
        backToMenuButton.setOnAction(event -> showMainMenu());
        treeSelector.setOnAction(event -> switchTree(treeSelector.getValue()));
        createButton.setOnAction(event -> execute(operationService.create()));
        insertButton.setOnAction(event -> handleInsert());
        deleteButton.setOnAction(event -> handleDelete());
        updateButton.setOnAction(event -> handleUpdate());
        searchButton.setOnAction(event -> handleSearch());
        traverseButton.setOnAction(event -> execute(operationService.traverse(traversalSelector.getValue())));
        undoButton.setOnAction(event -> execute(operationService.undo()));
        redoButton.setOnAction(event -> execute(operationService.redo()));
        playPauseButton.setOnAction(event -> togglePlayback());
        stepBackButton.setOnAction(event -> stepBackward());
        stepForwardButton.setOnAction(event -> stepForward());

        treeSelector.setValue(operationService.getCurrentTreeType());
        refreshInputHints();
        renderCurrentStep();
        updateUndoRedoButtons();
        showMainMenu();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void openTree(TreeType treeType) {
        switchTree(treeType);
        showVisualizer();
    }

    private void switchTree(TreeType treeType) {
        if (treeType == null) {
            return;
        }
        stopPlayback();
        execute(operationService.switchTree(treeType));
        treeSelector.setValue(treeType);
    }

    private void execute(OperationTrace trace) {
        stopPlayback();
        currentStepIndex = 0;
        refreshInputHints();
        updateUndoRedoButtons();
        renderTrace(trace);
    }

    private void handleInsert() {
        Integer value = parseRequired(valueField, "Insert value");
        if (value == null) {
            return;
        }
        Integer parent = InputValidator.parseOptionalInt(parentField);
        if (parentField.getText().trim().length() > 0 && parent == null) {
            setValidationMessage("Parent value must be an integer.");
            parentField.requestFocus();
            return;
        }
        if (operationService.getCurrentTreeType() == TreeType.GENERIC
                && operationService.getCurrentModel().snapshot().root() == null
                && parent != null) {
            setValidationMessage("Generic Tree is empty: leave Parent value blank so the new value becomes the root.");
            parentField.requestFocus();
            return;
        }
        if (operationService.getCurrentTreeType() == TreeType.GENERIC
                && operationService.getCurrentModel().snapshot().root() != null
                && parent == null) {
            setValidationMessage("Generic Tree insertion needs a parent value when the tree is not empty.");
            return;
        }
        execute(operationService.insert(parent, value));
    }

    private void handleDelete() {
        Integer value = parseRequired(valueField, "Delete value");
        if (value != null) {
            execute(operationService.delete(value));
        }
    }

    private void handleUpdate() {
        Integer currentValue = parseRequired(valueField, "Current value");
        Integer nextValue = parseRequired(newValueField, "New value");
        if (currentValue != null && nextValue != null) {
            execute(operationService.update(currentValue, nextValue));
        }
    }

    private void handleSearch() {
        Integer value = parseRequired(valueField, "Search value");
        if (value != null) {
            execute(operationService.search(value));
        }
    }

    private Integer parseRequired(TextField field, String label) {
        Integer value = InputValidator.parseRequiredInt(field);
        if (value == null) {
            setValidationMessage(label + " must be an integer.");
            field.requestFocus();
            return null;
        }
        setValidationMessage("Ready: " + label + " = " + value + ".");
        return value;
    }

    private void renderCurrentStep() {
        renderTrace(operationService.getCurrentTrace());
    }

    private void renderTrace(OperationTrace trace) {
        if (trace.steps().isEmpty()) {
            return;
        }
        OperationStep step = trace.steps().get(currentStepIndex);
        statusLabel.setText(step.title());
        stepLabel.setText(step.message());
        traceCounterLabel.setText("Step " + (currentStepIndex + 1) + " / " + trace.steps().size());
        progressBar.setProgress(trace.steps().size() == 1 ? 1 : (double) currentStepIndex / (trace.steps().size() - 1));
        renderPseudoCode(step.pseudocode(), step.activeLine());
        treeCanvas.render(step.snapshot());
        stepBackButton.setDisable(currentStepIndex == 0);
        stepForwardButton.setDisable(currentStepIndex >= trace.steps().size() - 1);
        playPauseButton.setDisable(trace.steps().size() <= 1);
    }

    private void renderPseudoCode(List<String> lines, int activeLine) {
        codePanel.getChildren().clear();
        for (int i = 0; i < lines.size(); i++) {
            Label line = new Label(String.format("%2d. %s", i + 1, lines.get(i)));
            line.setWrapText(true);
            line.getStyleClass().add(i == activeLine ? "code-line-active" : "code-line");
            codePanel.getChildren().add(line);
        }
    }

    private void togglePlayback() {
        if (playback != null && playback.getStatus() == Animation.Status.RUNNING) {
            stopPlayback();
            return;
        }
        playback = new Timeline(new KeyFrame(Duration.seconds(speedSlider.getValue()), event -> {
            OperationTrace trace = operationService.getCurrentTrace();
            if (currentStepIndex < trace.steps().size() - 1) {
                currentStepIndex++;
                renderTrace(trace);
            } else {
                stopPlayback();
            }
        }));
        playback.setCycleCount(Timeline.INDEFINITE);
        playback.play();
        playPauseButton.setText("Pause");
    }

    private void stopPlayback() {
        if (playback != null) {
            playback.stop();
            playback = null;
        }
        playPauseButton.setText("Play");
    }

    private void stepBackward() {
        stopPlayback();
        if (currentStepIndex > 0) {
            currentStepIndex--;
            renderCurrentStep();
        }
    }

    private void stepForward() {
        stopPlayback();
        if (currentStepIndex < operationService.getCurrentTrace().steps().size() - 1) {
            currentStepIndex++;
            renderCurrentStep();
        }
    }

    private void updateUndoRedoButtons() {
        undoButton.setDisable(!operationService.canUndo());
        redoButton.setDisable(!operationService.canRedo());
    }

    private void refreshInputHints() {
        TreeType treeType = operationService.getCurrentTreeType();
        boolean generic = treeType == TreeType.GENERIC;
        parentField.setDisable(!generic);
        parentField.setOpacity(generic ? 1 : 0.65);
        if (!generic) {
            parentField.setPromptText("Parent ignored");
            validationLabel.setText("Parent value is ignored for " + treeType.getDisplayName() + ".");
            return;
        }
        boolean emptyTree = operationService.getCurrentModel().snapshot().root() == null;
        parentField.setPromptText(emptyTree ? "Leave blank for root creation" : "Parent node value");
        validationLabel.setText(emptyTree
                ? "Generic Tree is empty: leave Parent blank and insert the first root value."
                : "Generic Tree rule: Parent is required once the tree already has a root.");
    }

    private void setValidationMessage(String message) {
        validationLabel.setText(message);
    }

    private void showMainMenu() {
        mainMenuView.setVisible(true);
        mainMenuView.setManaged(true);
        visualizerView.setVisible(false);
        visualizerView.setManaged(false);
    }

    private void showVisualizer() {
        mainMenuView.setVisible(false);
        mainMenuView.setManaged(false);
        visualizerView.setVisible(true);
        visualizerView.setManaged(true);
    }

    private void showHelp() {
        DialogHelper.showInfo(
                "Project guide",
                "Organized structure:\n" +
                "- controller: handles events and playback\n" +
                "- service: manages tree operations and history\n" +
                "- tree package: domain model\n" +
                "- util: shared helpers\n" +
                "- view/resources: FXML, CSS, and rendering components"
        );
    }
}
