package edu.uiuc.cs.charm;

public class Point
{
    int x,y;

    public Point(Point p)
    {
        x = p.x;
        y = p.y;
    }
    public Point(int _x, int _y)
    {
        x = _x;
        y = _y;
    }
    public Point()
    {
        x = 0;
        y = 0;
    }
    void copy(Point p)
    {
        x = p.x;
        y = p.y;
    }
}