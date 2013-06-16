package model;

import org.lwjgl.util.vector.Vector3f;

/**
 * User: hamdan
 * Date: 2013-06-15
 * Time: 11:55 PM
 */
public class Face {

	public Vector3f vertexIndices = new Vector3f();
	public Vector3f normalIndices  = new Vector3f();

	public Face(Vector3f vertexIndices, Vector3f normalIndices) {
		this.vertexIndices = vertexIndices;
		this.normalIndices = normalIndices;
	}

}
