package at.playify.boxgamereloaded.util;

@SuppressWarnings("unused")
public class BoundingBox3d extends BoundingBox {
    public float maxZ;
    public float minZ;

    public BoundingBox3d(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        super(minX, minY, maxX, maxY);
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public Borrow.BorrowedBoundingBox3d addCoord(float x, float y, float z) {
        float miX = this.minX;
        float miY = this.minY;
        float maX = this.maxX;
        float maY = this.maxY;
        float miZ = this.minZ;
        float maZ = this.maxZ;

        if (x < 0.0D) {
            miX += x;
        } else if (x > 0.0D) {
            maX += x;
        }

        if (y < 0.0D) {
            miY += y;
        } else if (y > 0.0D) {
            maY += y;
        }

        if (z < 0.0D) {
            miZ += z;
        } else if (z > 0.0D) {
            maZ += z;
        }
        return Borrow.bound3d(miX, miY, miZ, maX, maY, maZ);
    }

    public float calculateXOffset(BoundingBox3d bound, float offsetX) {
        if (bound.maxY > this.minY && bound.minY < this.maxY && bound.maxZ > this.minZ && bound.minZ < this.maxZ) {
            if (offsetX > 0.0f && bound.maxX <= this.minX) {
                float d1 = this.minX - bound.maxX;

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0f && bound.minX >= this.maxX) {
                float d0 = this.maxX - bound.minX;

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

            return offsetX;
        } else {
            return offsetX;
        }
    }

    public float calculateYOffset(BoundingBox3d bound, float offsetY) {
        if (bound.maxX > this.minX && bound.minX < this.maxX && bound.maxZ > this.minZ && bound.minZ < this.maxZ) {
            if (offsetY > 0.0f && bound.maxY <= this.minY) {
                float d1 = this.minY - bound.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0f && bound.minY >= this.maxY) {
                float d0 = this.maxY - bound.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }

    public float calculateZOffset(BoundingBox3d bound, float offsetZ) {
        if (bound.maxX > this.minX && bound.minX < this.maxX && bound.maxZ > this.minZ && bound.minZ < this.maxZ) {
            if (offsetZ > 0.0f && bound.maxZ <= this.minZ) {
                float d1 = this.minZ - bound.maxZ;

                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0f && bound.minZ >= this.maxZ) {
                float d0 = this.maxZ - bound.minZ;

                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

            return offsetZ;
        } else {
            return offsetZ;
        }
    }

    public void offset(float x, float y, float z) {
        minX += x;
        maxX += x;
        minY += y;
        maxY += y;
        minZ += z;
        maxZ += z;
    }

    public BoundingBox copyFrom(BoundingBox3d bound) {
        minX = bound.minX;
        minY = bound.minY;
        minZ = bound.minZ;
        maxX = bound.maxX;
        maxY = bound.maxY;
        maxZ = bound.maxZ;
        return this;
    }

    public String toString() {
        return "BoundingBox3d(" + minX + "->" + maxX + "," + minY + "->" + maxY + "," + minZ + "->" + maxZ + ")";
    }

    public void setSize(float w, float h, float d) {
        float x = (minX + maxX) / 2;
        float y = (minY + maxY) / 2;
        float z = (minZ + maxZ) / 2;
        w /= 2;
        h /= 2;
        d /= 2;
        minX = x - w;
        minY = y - h;
        minZ = z - d;
        maxX = x + w;
        maxY = y + h;
        maxZ = z + d;
    }

    public void set(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public float z() {
        return (minZ + maxZ) / 2;
    }

    public float z(float z) {
        float dif = (minZ - maxZ) / 2;
        minZ = z - dif;
        maxZ = z + dif;
        return z;
    }

    private boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }

    public boolean intersects(BoundingBox3d b) {
        return this.intersects(b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ);
    }
}
