package min3d.animation;

import min3d.core.Object3d;

public class AnimationObject3d extends Object3d {
	private int numFrames;
	private KeyFrame[] frames;
	private int currentFrameIndex;
	private long startTime;
	private long currentTime;
	private boolean isPlaying;
	private float interpolation;
	private float fps = 70;

	public AnimationObject3d(int $maxVertices, int $maxFaces, int $numFrames) {
		super($maxVertices, $maxFaces);
		this.numFrames = $numFrames;
		this.frames = new KeyFrame[numFrames];
		this.currentFrameIndex = 0;
		this.isPlaying = false;
		this.interpolation = 0;
		this._animationEnabled = true;
	}

	public int getCurrentFrame() {
		return currentFrameIndex;
	}

	public void addFrame(KeyFrame frame) {
		frames[currentFrameIndex++] = frame;
	}

	public void setFrames(KeyFrame[] frames) {
		this.frames = frames;
	}

	public void play() {
		startTime = System.currentTimeMillis();
		isPlaying = true;
	}

	public void play(String name) {
		currentFrameIndex = 0;

		for (int i = 0; i < numFrames; i++) {
			if (frames[i].getName().equals(name))
				currentFrameIndex = i;
		}

		play();
	}

	public void stop() {
		isPlaying = false;
		currentFrameIndex = 0;
	}

	public void pause() {
		isPlaying = false;
	}

	public void update() {
		if (!isPlaying)
			return;
		currentTime = System.currentTimeMillis();
		KeyFrame currentFrame = frames[currentFrameIndex];
		KeyFrame nextFrame = frames[(currentFrameIndex + 1) % numFrames];
		float[] currentVerts = currentFrame.getVertices();
		float[] nextVerts = nextFrame.getVertices();
		float[] currentNormals = currentFrame.getNormals();
		float[] nextNormals = nextFrame.getNormals();
		int numVerts = currentVerts.length;
		
		float[] interPolatedVerts = new float[numVerts];
		float[] interPolatedNormals = new float[numVerts];

		for (int i = 0; i < numVerts; i += 3) {
			interPolatedVerts[i] = currentVerts[i] + interpolation * (nextVerts[i] - currentVerts[i]);
			interPolatedVerts[i + 1] = currentVerts[i + 1] + interpolation * (nextVerts[i + 1] - currentVerts[i + 1]);
			interPolatedVerts[i + 2] = currentVerts[i + 2] + interpolation 	* (nextVerts[i + 2] - currentVerts[i + 2]);
			interPolatedNormals[i] = currentNormals[i] + interpolation * (nextNormals[i] - currentNormals[i]);
			interPolatedNormals[i + 1] = currentNormals[i + 1] + interpolation * (nextNormals[i + 1] - currentNormals[i + 1]);
			interPolatedNormals[i + 2] = currentNormals[i + 2] + interpolation * (nextNormals[i + 2] - currentNormals[i + 2]);
		}

		interpolation += fps * (currentTime - startTime) / 1000;
		
		vertices().overwriteNormals(interPolatedNormals);
		vertices().overwriteVerts(interPolatedVerts);
	
		if (interpolation > 1) {
			interpolation = 0;
			currentFrameIndex++;

			if (currentFrameIndex >= numFrames)
				currentFrameIndex = 0;
		}
		
		startTime = System.currentTimeMillis();
	}

	public float getFps() {
		return fps;
	}

	public void setFps(float fps) {
		this.fps = fps;
	}
}