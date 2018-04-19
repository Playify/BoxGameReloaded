package at.playify.boxgamereloaded.util;

import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.interfaces.Game;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

//Ausborgbares
//Alles hier sollte sobald nicht mehr benötigt mit free() wieder zurückgegeben werden.
//Dies spart neuerstellen und im Speicher behalten von alten Variablen
@SuppressWarnings({"WeakerAccess", "unused"})
public class Borrow {
    private static long borrowed=0;
    private static Queue<BorrowedCollisionData> datas = new ConcurrentLinkedQueue<>();
    private static Queue<BorrowedBoundingBox> bounds = new ConcurrentLinkedQueue<>();
    private static Queue<BorrowedBoundingBox3d> bounds3d = new ConcurrentLinkedQueue<>();
    private static Queue<ArrayList<? extends Borrowed>> boundLists = new ConcurrentLinkedQueue<>();

    public static <T extends Borrowed> void free(T b) {
        b.free();
    }

    public static void free(ArrayList<? extends Borrowed> list) {
        freeInside(list);
        boundLists.add(list);
    }

    public static void freeInside(ArrayList<? extends Borrowed> list) {
        Iterator<? extends Borrowed> iterator = list.iterator();
        while (iterator.hasNext()) {
            Borrowed next = iterator.next();
            next.free();
            iterator.remove();
        }
    }

    public static CollisionData data() {
        BorrowedCollisionData poll = datas.poll();
        if (poll == null) {
            borrowed++;
            return new BorrowedCollisionData();
        } else {
            poll.blk = null;
            poll.x = poll.y = -1;
            poll.meta = 0;
            return poll;
        }
    }

    public static BorrowedCollisionData data(Block blk, int x, int y, int meta) {
        BorrowedCollisionData poll = datas.poll();
        if (poll == null) {
            borrowed++;
            return new BorrowedCollisionData();
        } else {
            poll.blk = blk;
            poll.x = x;
            poll.y = y;
            poll.meta = meta;
            return poll;
        }
    }

    public static BorrowedBoundingBox bound(float minX,float minY,float maxX,float maxY) {
        BorrowedBoundingBox poll = bounds.poll();
        if (poll == null) {
            borrowed++;
            return new BorrowedBoundingBox(minX,minY,maxX,maxY);
        } else {
            poll.up=poll.down=poll.left=poll.right=true;
            poll.set(minX,minY,maxX,maxY);
            return poll;
        }
    }

    public static BorrowedBoundingBox3d bound3d(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        BorrowedBoundingBox3d poll = bounds3d.poll();
        if (poll == null) {
            borrowed++;
            return new BorrowedBoundingBox3d(minX, minY, minZ, maxX, maxY, maxZ);
        } else {
            poll.up = poll.down = poll.left = poll.right = true;
            poll.set(minX, minY, minZ, maxX, maxY, maxZ);
            return poll;
        }
    }


    public static BorrowedBoundingBox bound() {
        BorrowedBoundingBox poll = bounds.poll();
        if (poll == null) {
            borrowed++;
            return new BorrowedBoundingBox(0,0,0,0);
        } else {
            poll.up=poll.down=poll.left=poll.right=true;
            poll.set(0,0,0,0);
            return poll;
        }
    }

    public static BorrowedBoundingBox3d bound3d() {
        BorrowedBoundingBox3d poll = bounds3d.poll();
        if (poll == null) {
            borrowed++;
            return new BorrowedBoundingBox3d(0, 0, 0, 0, 0, 0);
        } else {
            poll.up = poll.down = poll.left = poll.right = poll.front = poll.back = true;
            poll.set(0, 0, 0, 0, 0, 0);
            return poll;
        }
    }

    public static <T extends Borrowed> ArrayList<T> boundList() {
        @SuppressWarnings("unchecked") ArrayList<T> poll=(ArrayList<T>) boundLists.poll();
        if (poll == null) {
            borrowed++;
            return new ArrayList<>();
        } else {
            if (!poll.isEmpty()) {
                freeInside(poll);
            }
            return poll;
        }
    }

    private static HashMap<Class,ConcurrentLinkedQueue> map=new HashMap<>();

    public static <T> T obj(Class<T> clazz) {
        ConcurrentLinkedQueue q = map.get(clazz);
        if (q != null) {
            //noinspection unchecked
            T poll = (T) q.poll();
            if (poll!=null) {
                return poll;
            }
        }
        try {
            borrowed++;
            return clazz.newInstance();
        } catch (Exception e) {
            Game.report(e);
            return null;
        }
    }
    public static void freeObj(Object o){
        Class clazz = o.getClass();
        ConcurrentLinkedQueue q = map.get(clazz);
        if (q==null) {
            map.put(clazz,q=new ConcurrentLinkedQueue());
        }
        //noinspection unchecked
        q.add(o);
    }

    public static String info(){
        StringBuilder ret= new StringBuilder("Borrow=" + borrowed + ", ");
        ret.append("Bound=").append(bounds.size());
        for (Map.Entry<Class, ConcurrentLinkedQueue> entry : map.entrySet()) {
            ret.append(", ").append(entry.getKey().getSimpleName()).append("=").append(entry.getValue().size());
        }
        return ret.toString();
    }

    public interface Borrowed {
        void free();
    }

    //Hier immer free() aufrufen, erst wenn nicht mehr benötigt
    public static class BorrowedBoundingBox extends BoundingBox implements Borrowed {
        public boolean up=true,down=true,left=false,right=true;//mit up,down,left,right können einseitig kollidierbare Wände gemacht werden.

        private BorrowedBoundingBox(float minX, float minY, float maxX, float maxY) {
            super(minX, minY, maxX, maxY);
        }

        public void free() {
            bounds.add(this);
        }

        @Override
        public float calculateXOffset(BoundingBox bound, float offsetX) {
            if (offsetX < 0 && !left) return offsetX;
            if (offsetX > 0 && !right) return offsetX;
            return super.calculateXOffset(bound, offsetX);
        }

        @Override
        public float calculateYOffset(BoundingBox bound, float offsetY) {
            if (offsetY < 0 && !down) return offsetY;
            if (offsetY > 0 && !up) return offsetY;
            return super.calculateYOffset(bound, offsetY);
        }
    }

    //Hier immer free() aufrufen, erst wenn nicht mehr benötigt
    public static class BorrowedBoundingBox3d extends BoundingBox3d implements Borrowed {
        public boolean up = true, down = true, left = false, right = true, front = true, back = true;//mit up,down,left,right können einseitig kollidierbare Wände gemacht werden.

        private BorrowedBoundingBox3d(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            super(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public void free() {
            bounds3d.add(this);
        }

        @Override
        public float calculateXOffset(BoundingBox bound, float offsetX) {
            if (offsetX<0&&!left)return offsetX;
            if (offsetX>0&&!right)return offsetX;
            return super.calculateXOffset(bound, offsetX);
        }

        @Override
        public float calculateYOffset(BoundingBox bound, float offsetY) {
            if (offsetY<0&&!down)return offsetY;
            if (offsetY>0&&!up)return offsetY;
            return super.calculateYOffset(bound, offsetY);
        }
    }

    public static class BorrowedCollisionData extends CollisionData implements Borrowed {
        public void free() {
            datas.add(this);
        }
    }
}
