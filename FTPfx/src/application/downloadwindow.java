package application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class downloadwindow extends Application {

	private FtpConnection ftpconnection;
	private String func;
	private String locallocation;
	private String ftplocation;
	private Label statusLabel;
	private Button cancelButton;
	private ProgressIndicator progressIndicator;
	private ProgressBar progressBar;
	private Label label;

	public downloadwindow(String function, FtpConnection ftp2, String locallocation, String ftplocation) {
		// TODO Auto-generated constructor stub
		this.ftpconnection = ftp2;
		this.locallocation = locallocation;
		this.ftplocation = ftplocation;
		this.func = function;
	}

	@Override
	public void start(Stage primaryStage) {

		label = new Label("Transferring files:");
		progressBar = new ProgressBar(0);
		progressIndicator = new ProgressIndicator(0);

		cancelButton = new Button("Cancel");

		statusLabel = new Label();
		statusLabel.setMinWidth(250);
		statusLabel.setTextFill(Color.BLUE);

		progressBar.setProgress(0);
		progressIndicator.setProgress(0);
		cancelButton.setDisable(false);

		// Create a Task.
		thread2 obj = new thread2(cancelButton, ftpconnection, ftplocation, locallocation, statusLabel, func);
		obj.start();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// Unbind progress property
				progressBar.progressProperty().unbind();

				// Bind progress property
				progressBar.progressProperty().bind(ftpconnection.progressProperty());

				// Unbind progress progress
				progressIndicator.progressProperty().unbind();

				// Bind progress property.
				progressIndicator.progressProperty().bind(ftpconnection.progressProperty());

				// Unbind text property for Label.
				statusLabel.textProperty().unbind();

				// Bind the text property of Label with message property of Task
				statusLabel.textProperty().bind(ftpconnection.messageProperty());

			}
		});

		// Start the Task.
		new Thread(ftpconnection).start();

		// Cancel
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// obj.interrupt();
				// Thread.currentThread().interrupt();
				Thread f = obj;
				Method m = null;
				try {
					m = Thread.class.getDeclaredMethod("stop0", new Class[] { Object.class });
				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m.setAccessible(true);

				try {
					m.invoke(f, new ThreadDeath());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Runtime.getRuntime().halt(1);
				primaryStage.close();
			}
		});

		FlowPane root = new FlowPane();
		root.setPadding(new Insets(10));
		root.setHgap(10);

		root.getChildren().addAll(label, progressBar, progressIndicator, statusLabel, cancelButton);

		Scene scene = new Scene(root, 400, 120, Color.WHITE);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				primaryStage.setTitle("Transfer window");
				primaryStage.setScene(scene);
				primaryStage.show();

			}
		});
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}

class thread2 extends Thread {
	private Button cancelButton;
	private FtpConnection ftpconnection2;
	private String ftplocation;
	private String locallocation;
	private Label statusLabel;
	private String func;

	public thread2(Button cancelButton1, FtpConnection ftpconnectio, String ftplocation1, String locallocation1,
			Label statusLabel1, String funcc) {
		cancelButton = cancelButton1;
		ftpconnection2 = ftpconnectio;
		ftplocation = ftplocation1;
		locallocation = locallocation1;
		statusLabel = statusLabel1;
		func = funcc;
	}

	public void run() {

		List<File> copied;
		try {

			if (func == "up") {
				List<File> a = ftpconnection2.uploadDirectory(ftplocation, locallocation, "");
				copied = a;
			} else {
				List<File> b = ftpconnection2.downloadDirectory(ftpconnection2, locallocation, ftplocation);
				copied = b;
			}

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					cancelButton.setText("close");
					statusLabel.textProperty().unbind();
					statusLabel.setText("trasfered: " + copied.size());
					try {

						homepage.creatTreeView(ftpconnection2);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}