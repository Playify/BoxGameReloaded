package at.playify.boxgamereloaded.util;

public class BoundingBox {
    public float minX;
    public float maxX;
    public float minY;
    public float maxY;

    public BoundingBox(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public Borrow.BorrowedBoundingBox addCoord(float x, float y) {
        float miX = this.minX;
        float miY = this.minY;
        float maX = this.maxX;
        float maY = this.maxY;

        if (x < 0.0D)
        {
            miX += x;
        }
        else if (x > 0.0D)
        {
            maX += x;
        }

        if (y < 0.0D)
        {
            miY += y;
        }
        else if (y > 0.0D)
        {
            maY += y;
        }

        return Borrow.bound(miX,miY,maX,maY);
    }

    @SuppressWarnings("WeakerAccess")
    public float calculateXOffset(BoundingBox bound, float offsetX) {
        if (bound.maxY>this.minY&&bound.minY<this.maxY) {
            if (offsetX>0.0f&&bound.maxX<=this.minX) {
                float d1 = this.minX - bound.maxX;

                if (d1<offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX<0.0f&&bound.minX >= this.maxX) {
                float d0 = this.maxX - bound.minX;

                if (d0>offsetX) {
                    offsetX = d0;
                }
            }

            return offsetX;
        } else {
            return offsetX;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public float calculateYOffset(BoundingBox bound, float offsetY) {
        if (bound.maxX>this.minX&&bound.minX<this.maxX) {
            if (offsetY>0.0f&&bound.maxY<=this.minY) {
                float d1 = this.minY - bound.maxY;

                if (d1<offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY<0.0f&&bound.minY >= this.maxY) {
                float d0 = this.maxY - bound.minY;

                if (d0>offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }

    public void offset(float x,float y){
        minX+=x;
        maxX+=x;
        minY+=y;
        maxY+=y;
    }

    public String toString() {
        return "BoundingBox("+minX+"->"+maxX+","+minY+"->"+maxY+")";
    }

    public void setSize(float w,float h) {
        float x= (minX+maxX)/2;
        float y= (minY+maxY)/2;
        w/=2;
        h/=2;
        minX=x-w;
        minY=y-h;
        maxX=x+w;
        maxY=y+h;
    }

    public void set(float minX, float minY, float maxX, float maxY) {
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
    }

    public float x() {
        return (minX+maxX)/2;
    }

    public float y() {
        return (minY+maxY)/2;
    }

    public float x(float x) {
        float dif=(minX-maxX)/2;
        minX=x-dif;
        maxX=x+dif;
        return x;
    }

    public float y(float y) {
        float dif=(minY-maxY)/2;
        minY=y-dif;
        maxY=y+dif;
        return y;
    }

    private boolean intersects(double minX, double minY, double maxX, double maxY)
    {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY;
    }

    public boolean intersects(BoundingBox b) {
        return this.intersects(b.minX, b.minY, b.maxX, b.maxY);
    }

    public boolean contains(float x, float y) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

    public boolean isEmpty() {
        return minX==maxX&&minY==maxY;
    }
}
