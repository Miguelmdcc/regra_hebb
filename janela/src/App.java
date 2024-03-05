import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import HebbResult.HebbResult;

public class App extends Application {

    private static final int SIZE = 8;
    private boolean[][] matrix1 = new boolean[SIZE][SIZE];
    private boolean[][] matrix2 = new boolean[SIZE][SIZE];
    private Rectangle[][] rectangles1 = new Rectangle[SIZE][SIZE];
    private Rectangle[][] rectangles2 = new Rectangle[SIZE][SIZE];
    private boolean[][][] trainingData = new boolean[2][SIZE][SIZE];
    HebbResult val = new HebbResult(null, 0);

    private Label label1;
    private Label label2;
    private TextField textField1;
    private TextField textField2;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Inicializando os labels
        label1 = new Label("Resultado: ");
        label2 = new Label("Resultado: ");

        // Criando VBox para cada matriz com seu respectivo label
        VBox matrix1Box = new VBox(5);
        matrix1Box.setAlignment(Pos.CENTER);
        matrix1Box.getChildren().addAll(label1, createGridPane(matrix1, rectangles1));

        VBox matrix2Box = new VBox(5);
        matrix2Box.setAlignment(Pos.CENTER);
        matrix2Box.getChildren().addAll(label2, createGridPane(matrix2, rectangles2));

        HBox matricesBox = new HBox(20);
        matricesBox.getChildren().addAll(matrix1Box, matrix2Box);
        matricesBox.setPadding(new Insets(0, 50, 0, 50)); // Adicionando um espaçamento

        Button clearButton = new Button("Limpar Matrizes");
        clearButton.setOnAction(e -> {
            clearMatrix(matrix1, rectangles1);
            clearMatrix(matrix2, rectangles2);
        });

        Button trainButton = new Button("Treino");
        trainButton.setOnAction(e -> {
            val = saveTrainingData();
        });

        Button testButton = new Button("Teste");
        testButton.setOnAction(e -> {
            test(val.w, val.b, textField1.getText(), textField2.getText());
        });

        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(clearButton, trainButton, testButton);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        buttonsBox.setAlignment(Pos.CENTER);

        // Inicialize os campos de texto
        textField1 = new TextField();
        textField2 = new TextField();

        // Adicione os campos de texto à interface gráfica
        VBox textFieldsBox = new VBox(5);
        textFieldsBox.setAlignment(Pos.CENTER);
        textFieldsBox.getChildren().addAll(new Label("Valor 1:"), textField1, new Label("Valor 2:"), textField2);

        root.getChildren().addAll(matricesBox, textFieldsBox, buttonsBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Draw Letters");
        primaryStage.show();
    }

    private Pane createGridPane(boolean[][] matrix, Rectangle[][] rectangles) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Rectangle rectangle = new Rectangle(30, 30);
                rectangle.setStroke(Color.BLACK);
                rectangle.setFill(Color.WHITE);

                final int x = i;
                final int y = j;

                rectangle.setOnMouseClicked(event -> {
                    toggleCell(matrix, rectangles, x, y);
                });

                rectangles[i][j] = rectangle;
                gridPane.add(rectangle, j, i);
            }
        }

        container.getChildren().addAll(gridPane);

        return container;
    }

    private void clearMatrix(boolean[][] matrix, Rectangle[][] rectangles) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = false;
                rectangles[i][j].setFill(Color.WHITE);
            }
        }
    }

    private void toggleCell(boolean[][] matrix, Rectangle[][] rectangles, int x, int y) {
        matrix[x][y] = !matrix[x][y];
        rectangles[x][y].setFill(matrix[x][y] ? Color.BLACK : Color.WHITE);
    }

    private static float[] matrixToFloatVector(boolean[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        float[] vector = new float[rows * cols];

        int k = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                vector[k++] = matrix[i][j] ? 1.0f : 0.0f; // Convertendo booleano em float
            }
        }

        return vector;
    }

    private HebbResult saveTrainingData() {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(matrix1[i], 0, trainingData[0][i], 0, SIZE);
            System.arraycopy(matrix2[i], 0, trainingData[1][i], 0, SIZE);
        }
        float[] vector1 = matrixToFloatVector(matrix1);
        float[] vector2 = matrixToFloatVector(matrix2);

        float[][] entrada = new float[][] { vector1, vector2 };

        // Exibir as matrizes salvas (apenas para fins de verificação)
        System.out.println("Matriz 1:");
        printMatrix(trainingData[0]);
        System.out.println("Matriz 2:");
        printMatrix(trainingData[1]);
        System.out.println("Matriz:");
        for (float[] row : entrada) {
            for (float cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        float[] target = new float[] { 1, -1 }; // Valor desejado para o primeiro padrão, -1 para o segundo

        // Atualização dos pesos pela regra de Hebb
        float[] w = new float[64]; // 8x8 = 64
        float b = 0; // Bias

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 64; j++) {
                w[j] += entrada[i][j] * target[i]; // Atualização do peso
            }
            b += target[i]; // Atualização do bias
        }
        System.out.println("Treino feito!");
        // Teste
        // for (int i = 0; i < 2; i++) {
        // float sum = b;
        // for (int j = 0; j < 64; j++) {
        // sum += w[j] * entrada[i][j];
        // }
        // int result = sum >= 0 ? 1 : -1;
        // System.out.println("Esperado: " + target[i]);
        // System.out.println("Encontrado: " + result);
        // }
        return new HebbResult(w, b);
    }

    private void test(float[] w, float b, String valor1, String valor2) {
        // Testando a primeira matriz
        float[] vector1 = matrixToFloatVector(matrix1);
        int result1 = getResult(vector1, w, b);

        // Testando a segunda matriz
        float[] vector2 = matrixToFloatVector(matrix2);
        int result2 = getResult(vector2, w, b);

        // Determinando qual valor imprimir com base no resultado
        if (result1 == 1) {
            label1.setText("Resultado da Matriz 1: " + valor1);
        } else {
            label1.setText("Resultado da Matriz 1: " + valor2);
        }

        if (result2 == 1) {
            label2.setText("Resultado da Matriz 2: " + valor1);
        } else {
            label2.setText("Resultado da Matriz 2: " + valor2);
        }
    }

    private int getResult(float[] vector, float[] w, float b) {
        float sum = b;
        for (int j = 0; j < 64; j++) {
            sum += w[j] * vector[j];
        }
        return sum >= 0 ? 1 : -1;
    }

    private void printMatrix(boolean[][] matrix) {
        for (boolean[] row : matrix) {
            for (boolean cell : row) {
                System.out.print(cell ? "1 " : "0 ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
