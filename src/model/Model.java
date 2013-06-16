package model;

import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hamdan
 * Date: 2013-06-15
 * Time: 11:53 PM
 */
public class Model {

	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals  = new ArrayList<Vector3f>();
	public List<Face>     faces    = new ArrayList<Face>();

	public Model() {

	}

}
