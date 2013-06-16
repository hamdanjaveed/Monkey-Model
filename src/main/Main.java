package main;

import camera.Camera;
import model.Face;
import model.Model;
import model.OBJLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class Main {

	// display constants
	private static final int    DISPLAY_WIDTH     = 1280;
	private static final int    DISPLAY_HEIGHT    = 720;
	private static final String DISPLAY_TITLE     = "Monkey Model";
	private static final int    TARGET_FRAME_RATE = 60;

	// perspective constants
	private static final float FIELD_OF_VIEW = 65.0f;
	private static final float NEAR_PLANE    = 0.001f;
	private static final float FAR_PLANE     = 100.0f;

	// shaders
	//private int shaderProgram;
	//private int vertexShader;
	//private int fragmentShader;

	private Camera camera;

	private long lastFrameSystemTime;

	private int monkeyDisplayListHandle;

	public Main() {
		initializeProgram();
		programLoop();
		exitProgram();
	}

	private void initializeProgram() {
		initializeDisplay();
		initializeGL();
		initializeVariables();
	}

	private void initializeDisplay() {
		// create the display
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.setTitle(DISPLAY_TITLE);
			Display.create();
			Mouse.setGrabbed(true);
		} catch(LWJGLException exception) {
			exception.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
	}

	private void initializeGL() {
		// edit the projection matrix
		glMatrixMode(GL_PROJECTION);
		// reset the projection matrix
		glLoadIdentity();

		gluPerspective(FIELD_OF_VIEW, (float) (DISPLAY_WIDTH / DISPLAY_HEIGHT), NEAR_PLANE, FAR_PLANE);

		// switch back to the model view matrix
		glMatrixMode(GL_MODELVIEW);

		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		glEnable(GL_DEPTH_TEST);

		// UNCOMMENT: if you want to use shaders
		//initializeShaders();
	}

	// UNCOMMENT: if you want to use shaders
	/*
	private void initializeShaders() {
		// create the program and shaders
		shaderProgram = glCreateProgram();
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

		// get a string representation of the shader
		StringBuilder vertexShaderSource = loadShaderSourceFromPath("src/shader/shader.vertex");
		StringBuilder fragmentShaderSource = loadShaderSourceFromPath("src/shader/shader.fragment");

		// compile the shaders
		compileShader(vertexShader, vertexShaderSource);
		compileShader(fragmentShader, fragmentShaderSource);

		// attach the shaders to the program
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);

		// link and validate the program
		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);
	}

	private StringBuilder loadShaderSourceFromPath(String path) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			exitProgramWithErrorCode(2);
		}
		return shaderSource;
	}

	private void compileShader(int shaderHandle, StringBuilder shaderSource) {
		glShaderSource(shaderHandle, shaderSource);
		glCompileShader(shaderHandle);
		if (glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Not able to compile shader " + shaderHandle + "\n" + "With source: " + shaderSource);
	}*/

	private void initializeVariables() {
		camera = new Camera(0.0f, 0.0f, 0.0f);

		// UNCOMMENT: if you want to use delta
		// getDelta();

		initializeModel();
	}

	private void initializeModel() {
		monkeyDisplayListHandle = glGenLists(1);
		glNewList(monkeyDisplayListHandle, GL_COMPILE); {
			Model model = null;

			try {
				model = OBJLoader.loadModelFromFile(new File("src/model/monkey.obj"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				exitProgramWithErrorCode(1);
			} catch (IOException e) {
				e.printStackTrace();
				exitProgramWithErrorCode(2);
			}

			glBegin(GL_TRIANGLES); {
				glColor3f(1, 1, 1);
				for (Face face : model.faces) {
					Vector3f normal1 = model.normals.get((int) face.normalIndices.x - 1);
					glNormal3f(normal1.x, normal1.y, normal1.z);
					Vector3f vertex1 = model.vertices.get((int) face.vertexIndices.x - 1);
					glVertex3f(vertex1.x, vertex1.y, vertex1.z);

					Vector3f normal2 = model.normals.get((int) face.normalIndices.y - 1);
					glNormal3f(normal2.x, normal2.y, normal2.z);
					Vector3f vertex2 = model.vertices.get((int) face.vertexIndices.y - 1);
					glVertex3f(vertex2.x, vertex2.y, vertex2.z);

					Vector3f normal3 = model.normals.get((int) face.normalIndices.z - 1);
					glNormal3f(normal3.x, normal3.y, normal3.z);
					Vector3f vertex3 = model.vertices.get((int) face.vertexIndices.z - 1);
					glVertex3f(vertex3.x, vertex3.y, vertex3.z);
				}
			} glEnd();
		} glEndList();
	}

	private void programLoop() {
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			// UNCOMMENT: if you want to use shaders
			// glUseProgram(shaderProgram);

			int delta = getDelta();
			renderGL();
			update(delta);

			// UNCOMMENT: if you want to use shaders
			//glUseProgram(0);
			Display.update();
			Display.sync(TARGET_FRAME_RATE);
		}
	}

	private void renderGL() {
		// clear both buffers
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glCallList(monkeyDisplayListHandle);

		glLoadIdentity();
		camera.lookThrough();
	}

	private void update(int delta) {
		camera.yawBy(Mouse.getDX() * delta * Camera.YAW_AND_PITCH_SPEED);
		camera.pitchBy(- Mouse.getDY() * delta * Camera.YAW_AND_PITCH_SPEED);

		float speedMultiplier = 1.0f;
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			speedMultiplier *= 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			camera.walkForward(Camera.MOVE_SPEED * delta * speedMultiplier);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			camera.walkBackwards(Camera.MOVE_SPEED * delta * speedMultiplier);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			camera.strafeLeft(Camera.MOVE_SPEED * delta * speedMultiplier);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			camera.strafeRight(Camera.MOVE_SPEED * delta * speedMultiplier);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			camera.flyUp(Camera.MOVE_SPEED * delta * speedMultiplier);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			camera.flyDown(Camera.MOVE_SPEED * delta * speedMultiplier);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			Mouse.setGrabbed(!Mouse.isGrabbed());
		}
	}

	// uncomment if you need to create FloatBuffers
	/*private FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
		floatBuffer.put(data);
		floatBuffer.flip();
		return floatBuffer;
	}*/

	private void exitProgram() {
		destroyBuffers();

		// UNCOMMENT: if you want to use shaders
		//destroyShaders();

		Display.destroy();
		System.exit(0);
	}

	private void exitProgramWithErrorCode(int error) {
		System.err.println("Terminated with error code " + error);
		destroyBuffers();
		Display.destroy();
		System.exit(error);
	}

	private void destroyBuffers() {
		// TODO: destroy buffers
	}

	// UNCOMMENT: if you want to use shaders
	/*
	private void destroyShaders() {
		glDeleteProgram(shaderProgram);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}*/


	private long getSystemTimeInMilliseconds() {
		// return the system time in ms
		return System.nanoTime() / 1000000;
	}

	private int getDelta() {
		long time = getSystemTimeInMilliseconds();
		int delta = (int) (time - lastFrameSystemTime);
		lastFrameSystemTime = time;
		return delta;
	}

	public static void main(String[] args) {
		new Main();
	}

}
