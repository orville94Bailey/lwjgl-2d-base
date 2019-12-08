package bulk;

import Interfaces.IDrawable;
import Rendering.Renderer;
import bulk.Player;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public class Map implements IDrawable{
    public Map(int height, int width){
        random = new Random();
        this.height = height;
        this.width = width;
        tiles = new ArrayList<>();
        for (int i = 0; i < (getCellColumns()*getCellRows())-1; i++) {
            if (i == 0) {
                tiles.add(new Tile(true));
            }
            if (random.nextInt()%10 == 0)
            {
                tiles.add(new Tile(false));
            } else {
                tiles.add(new Tile(true));
            }
        }
        player = new Player();
        colors = new ArrayList<>();
        colors.add(new Triplet<>(1f,0f,0f));
        colors.add(new Triplet<>(0f,1f,0f));
        colors.add(new Triplet<>(0f,0f,1f));
        colors.add(new Triplet<>(0f,1f,1f));
        colors.add(new Triplet<>(1f,0f,1f));
    }

    public Map(){
        this(100,100);
    }

    @Override
    public void DrawMe() {
        for (int i = 0; i < tiles.size(); i++) {
            Renderer.DrawQuad(
                    getRenderCoordsFromIndex(i),
                    tileWidthHolder,
                    tiles.get(i).canBeMovedThrough() ?
                            colors.get(tiles.get(i).getActivations()%colors.size()):
                            new Triplet<>(0f,0f,0f)
                    );
        }
        player.DrawMe();
    }

    protected List<Tile> tiles;

    protected int height;
    protected int width;
    protected Pair<Float,Float> tileWidthHolder = new Pair<>((float)Tile.TILE_WIDTH, (float)Tile.TILE_WIDTH);
    private ArrayList<Triplet<Float,Float,Float>> colors;
    private Random random;
    protected Player player;

    protected int getCellRows(){
        return height / Tile.TILE_WIDTH;
    }
    protected int getCellColumns(){
        return  width/ Tile.TILE_WIDTH;
    }
    private Pair<Integer, Integer> getRenderCoordsFromIndex(int index) {
        return new Pair<>(index%getCellColumns()*Tile.TILE_WIDTH+(Tile.TILE_WIDTH/2),index/getCellColumns()*Tile.TILE_WIDTH+(Tile.TILE_WIDTH/2));
    }

    private Pair<Integer,Integer> getCoordsFromIndex(int index){
        return new Pair<>(index%getCellColumns(), index/getCellColumns());
    }

    protected int getIndexFromCoords(int x, int y) {
        return y*getCellColumns()+x;
    }

    public void UpdatePlayer(int key) {
        ArrayList<Tile> applicableTiles = new ArrayList<>();
        switch (key)
        {
            case GLFW_KEY_LEFT:
                if (player.xPos > 0 &&
                        this.tiles.get(getIndexFromCoords(player.xPos-1, player.yPos)).canBeMovedThrough()) {
                    player.xPos -= 1;
                }
                break;
            case GLFW_KEY_UP:
                if (player.yPos < getCellRows()-1&&
                        this.tiles.get(getIndexFromCoords(player.xPos, player.yPos+1)).canBeMovedThrough()) {
                        player.yPos += 1;
                }
                break;
            case GLFW_KEY_RIGHT:
                if (player.xPos < getCellColumns()-1 &&
                        this.tiles.get(getIndexFromCoords(player.xPos+1, player.yPos)).canBeMovedThrough()) {
                        player.xPos += 1;
                }
                break;
            case GLFW_KEY_DOWN:
                if (player.yPos > 0 &&
                        this.tiles.get(getIndexFromCoords(player.xPos, player.yPos-1)).canBeMovedThrough()) {
                        player.yPos -= 1;
                }
                break;
            case GLFW_KEY_LEFT_ALT:
                random.ints(262,266).limit(30).forEach( i -> UpdatePlayer(i));
                break;
        }
        applicableTiles.forEach(tile -> tile.Activate());
    }

    //get cells in column
    private ArrayList<Tile> getTilesInColumn(int column) {
        ArrayList<Tile> holder = new ArrayList<>();

        for (int i = column; i < tiles.size(); i+= getCellColumns()) {
            holder.add(tiles.get(i));
        }
        return holder;
    }
    //get cells in row
    protected List<Tile> getTilesInRow(int row) {
        return new ArrayList<>( tiles.subList(row*getCellColumns(),(row*getCellColumns())+getCellColumns()));
    }

    private int coordsToIndex(int x, int y) {
        return (y*getCellColumns())+x;
    }
}
