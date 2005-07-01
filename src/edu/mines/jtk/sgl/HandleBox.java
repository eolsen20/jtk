/****************************************************************************
Copyright (c) 2004, Colorado School of Mines and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is 
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package edu.mines.jtk.sgl;

import java.awt.*;
import java.nio.*;

import edu.mines.jtk.util.*;
import static edu.mines.jtk.opengl.Gl.*;


/**
 * An axis-aligned handle box.
 * @author Dave Hale, Colorado School of Mines
 * @version 2005.06.30
 */
public class HandleBox extends Handle {

  /**
   * Constructs a handle box with identity transform.
   */
  public HandleBox() {
    this.addChild(_box);
  }

  /**
   * Sets the color of all handle boxes.
   * @param color the color.
   */
  public static void setColor(Color color) {
    _colorState.setColor(color);
    _box.dirtyDraw();
  }

  public void beginSelect(PickResult pr) {
    System.out.println("HandleBox.beginSelect");
  }

  ///////////////////////////////////////////////////////////////////////////
  // protected

  protected double getSize() {
    return _size;
  }

  ///////////////////////////////////////////////////////////////////////////
  // private

  private void drawBox(DrawContext dc) {
    Matrix44 transform = computeTransform(dc);
    glMultMatrixd(transform.m);
    glPushClientAttrib(GL_CLIENT_VERTEX_ARRAY_BIT);
    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_NORMAL_ARRAY);
    glVertexPointer(3,GL_FLOAT,0,_vb);
    glNormalPointer(GL_FLOAT,0,_nb);
    glDrawArrays(GL_QUADS,0,24);
    glPopClientAttrib();
  }

  private void pickBox(PickContext pc) {
    Matrix44 transform = computeTransform(pc);
    PickSegment ps = pc.getPickSegment().clone();
    ps.transform(transform.inverse());
    for (int iside=0,index=0; iside<6; ++iside,index+=12) {
      double xa = _va[index+ 0];
      double ya = _va[index+ 1];
      double za = _va[index+ 2];
      double xb = _va[index+ 3];
      double yb = _va[index+ 4];
      double zb = _va[index+ 5];
      double xc = _va[index+ 6];
      double yc = _va[index+ 7];
      double zc = _va[index+ 8];
      double xd = _va[index+ 9];
      double yd = _va[index+10];
      double zd = _va[index+11];
      Point3 p = ps.intersectWithTriangle(xa,ya,za,xb,yb,zb,xc,yc,zc);
      Point3 q = ps.intersectWithTriangle(xa,ya,za,xc,yc,zc,xd,yd,zd);
      if (p!=null)
        pc.addResult(transform.times(p));
      if (q!=null)
        pc.addResult(transform.times(q));
    }
  }

  private static class Box extends Node {

    Box() {
      StateSet states = new StateSet();
      _materialState = new MaterialState();
      _materialState.setColorMaterialFront(GL_AMBIENT_AND_DIFFUSE);
      _materialState.setSpecularFront(Color.white);
      _materialState.setShininessFront(100.0f);
      states.add(_materialState);
      _colorState = new ColorState();
      _colorState.setColor(Color.YELLOW);
      states.add(_colorState);
      setStates(states);
    }

    protected void draw(DrawContext dc) {
      HandleBox hb = (HandleBox)dc.getNode(-2);
      hb.drawBox(dc);
    }

    protected void pick(PickContext pc) {
      HandleBox hb = (HandleBox)pc.getNode(-2);
      hb.pickBox(pc);
    }
  }

  private static Box _box = new Box(); // the one box
  private static double _size = 10; // size of box in pixels
  private static ColorState _colorState = new ColorState();
  private static MaterialState _materialState = new MaterialState();

  // Vertices and normals.
  private static float[] _va = {
    -1.0f,-1.0f,-1.0f, -1.0f,-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f,-1.0f,
    -1.0f,-1.0f,-1.0f,  1.0f,-1.0f,-1.0f,  1.0f,-1.0f, 1.0f, -1.0f,-1.0f, 1.0f,
    -1.0f,-1.0f,-1.0f, -1.0f, 1.0f,-1.0f,  1.0f, 1.0f,-1.0f,  1.0f,-1.0f,-1.0f,
     1.0f,-1.0f,-1.0f,  1.0f, 1.0f,-1.0f,  1.0f, 1.0f, 1.0f,  1.0f,-1.0f, 1.0f,
    -1.0f, 1.0f,-1.0f, -1.0f, 1.0f, 1.0f,  1.0f, 1.0f, 1.0f,  1.0f, 1.0f,-1.0f,
    -1.0f,-1.0f, 1.0f,  1.0f,-1.0f, 1.0f,  1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,
  };
  private static float[] _na = {
    -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
     0.0f,-1.0f, 0.0f,  0.0f,-1.0f, 0.0f,  0.0f,-1.0f, 0.0f,  0.0f,-1.0f, 0.0f,
     0.0f, 0.0f,-1.0f,  0.0f, 0.0f,-1.0f,  0.0f, 0.0f,-1.0f,  0.0f, 0.0f,-1.0f,
     1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,
     0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,
     0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,
  };
  private static FloatBuffer _vb = Direct.newFloatBuffer(_va);
  private static FloatBuffer _nb = Direct.newFloatBuffer(_na);
}