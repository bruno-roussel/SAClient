package helpers;

import valueobjects.RoundStartInfo;
import valueobjects.Sphere;

public class Collision {

	/**
	 * detects the collisions between spheres, then handle them
	 * 
	 * @param aliveSpheres
	 *            the list of spheres in the arena
	 */
	public void handleCollisions(Sphere[] aliveSpheres,
			RoundStartInfo roundStartInfo) {
		int sphereNumber = aliveSpheres.length;
		for (int i = 0; i < sphereNumber; i++) {
			Sphere sphere = aliveSpheres[i];
			for (int j = i + 1; j < sphereNumber; j++) {
				Sphere otherSphere = aliveSpheres[j];
				int distX = otherSphere.x - sphere.x;
				int distY = otherSphere.y - sphere.y;
				int diameter = roundStartInfo.sphereRadius * 2;
				int distance = (int) Math.sqrt(distX * distX + distY * distY);
				if (distance < diameter) {
					repulse(sphere, otherSphere, distX, distY, distance,
							roundStartInfo);
				}
			}
		}
	}

	/**
	 * Modify speed vectors of two colliding balls
	 * 
	 * @param sphere
	 *            the first colliding ball
	 * @param otherSphere
	 *            the second colliding ball
	 * @param distX
	 *            distance in pixels between the centers of the sphere, along
	 *            the X axis
	 * @param distY
	 *            distance in pixels between the centers of the sphere, along
	 *            the Y axis
	 * @param distance
	 *            the distance in pixels between the center of the spheres
	 */
	private void repulse(Sphere sphere, Sphere otherSphere, int distX,
			int distY, int distance, RoundStartInfo roundInfo) {
		// define unitary direction vector
		int normalX = distX / distance;
		int normalY = distY / distance;
		// the collision point
		int middlepointX = (sphere.x + otherSphere.x) / 2;
		int middlepointY = (sphere.y + otherSphere.y) / 2;
		// replace spheres to ensure they dont overlap
		sphere.x = middlepointX - normalX * roundInfo.sphereRadius;
		sphere.y = middlepointY - normalY * roundInfo.sphereRadius;
		otherSphere.x = middlepointX + normalX * roundInfo.sphereRadius;
		otherSphere.y = middlepointY + normalY * roundInfo.sphereRadius;
		// compute adjustement vector
		int adjustment = ((sphere.vx - otherSphere.vx) * normalX)
				+ ((sphere.vy - otherSphere.vy) * normalY);
		int adjustmentX = adjustment * normalX;
		int adjustementY = adjustment * normalY;
		// apply adjustment to velocity vectors
		sphere.vx -= adjustmentX;
		sphere.vy -= adjustementY;
		otherSphere.vx += adjustmentX;
		otherSphere.vy += adjustementY;
	}

}
