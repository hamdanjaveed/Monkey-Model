package model;

import org.lwjgl.util.vector.Vector3f;

import java.io.*;

/**
 * User: hamdan
 * Date: 2013-06-15
 * Time: 11:56 PM
 */
public class OBJLoader {

	public static Model loadModelFromFile(File file) throws FileNotFoundException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		Model model = new Model();

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] splitLine = line.split(" ");
			if (line.startsWith("v ")) {
				float x = Float.valueOf(splitLine[1]);
				float y = Float.valueOf(splitLine[2]);
				float z = Float.valueOf(splitLine[3]);
				model.vertices.add(new Vector3f(x, y, z));
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(splitLine[1]);
				float y = Float.valueOf(splitLine[2]);
				float z = Float.valueOf(splitLine[3]);
				model.normals.add(new Vector3f(x, y, z));
			} else if (line.startsWith("f")) {
				Vector3f vertexIndices = new Vector3f(Float.valueOf(splitLine[1].split("/")[0]),
													  Float.valueOf(splitLine[2].split("/")[0]),
													  Float.valueOf(splitLine[3].split("/")[0]));
				Vector3f normalIndices = new Vector3f(Float.valueOf(splitLine[1].split("/")[2]),
													  Float.valueOf(splitLine[2].split("/")[2]),
													  Float.valueOf(splitLine[3].split("/")[2]));

				model.faces.add(new Face(vertexIndices, normalIndices));
			}
		}

		bufferedReader.close();

		return model;
	}

}
