import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

@SuppressWarnings("serial")
abstract class Shape implements Serializable{
   int startX;
   int startY;
   Color color;
   int lastX;
   int lastY;
   int Thickness;
   boolean fillFlag;
   abstract void draw(Graphics g);
   abstract void draw(Graphics g, int x, int y, int x2, int y2);
   
   public Shape() {
	// TODO Auto-generated constructor stub
	startX = startY = 0;
	lastX = lastY = 0;   
	color = new Color(255,255,255);
	fillFlag = false;
   }
 
   void setCoordinate(int x, int y, int x2, int y2) {
      startX = x;
      startY = y;
      lastX = x2;
      lastY = y2;
   }

   void setColor(Color color) {
      this.color = color;
   }
   
   void setThickness(int Thickness) {
	   this.Thickness = Thickness;
   }

   static Shape create(String selected)  
   {
      if(selected.equals("Line"))
         return new Line();
      if(selected.equals("Oval"))
         return new Oval();
      if(selected.equals("Rect"))
         return new Rect();
      else
         return null;
   }
}

@SuppressWarnings("serial")
class Line extends Shape implements Serializable{
   void draw(Graphics g) 
   {
	  Graphics2D g2 = (Graphics2D)g;
  	  g2.setStroke(new BasicStroke(Thickness, BasicStroke.CAP_ROUND, 0));
      g2.setColor(color);
      g2.drawLine(startX, startY, lastX, lastY);
   }

   void draw(Graphics g, int x, int y, int x2, int y2) 
   {
      g.setColor(color);
      g.drawLine(x, y, x2, y2);
   }
}

@SuppressWarnings("serial")
class Oval extends Shape implements Serializable{
   void draw(Graphics g) 
   {
	  Graphics2D g2 = (Graphics2D)g;
	  g2.setStroke(new BasicStroke(Thickness, BasicStroke.CAP_ROUND, 0));
	  g2.setColor(color);
	  if(fillFlag == true)
	  {
		 g2.fillOval(Math.min(startX, lastX), Math.min(startY, lastY),
    		  Math.abs(lastX - startX), Math.abs(lastY - startY));
		 
	  }
	  else
	  {
		
		  g2.drawOval(Math.min(startX, lastX), Math.min(startY, lastY),
    		  Math.abs(lastX - startX), Math.abs(lastY - startY)); 
		  
	  }
   }

   void draw(Graphics g, int x, int y, int x2, int y2) 
   {
	   Graphics2D g2 = (Graphics2D)g;
		  g2.setStroke(new BasicStroke(Thickness, BasicStroke.CAP_ROUND, 0));
		  g2.setColor(color);
		  if(fillFlag == true)
		  {
			 g2.fillOval(Math.min(x, x2), Math.min(y, y2),
	    		  Math.abs(x2 - x), Math.abs(y2 - y));
			 
		  }
		  else
		  {
			
			  g2.drawOval(Math.min(x, x2), Math.min(y, y2),
	    		  Math.abs(x2 - x), Math.abs(y2 - y));
		  }
   }
}

@SuppressWarnings("serial")
class Rect extends Shape implements Serializable{
   void draw(Graphics g)
   {
	  Graphics2D g2 = (Graphics2D)g;
	  g2.setStroke(new BasicStroke(Thickness, BasicStroke.CAP_ROUND, 0));
      g2.setColor(color);
      if(fillFlag == true)
      {
    	  g2.fillRect(Math.min(startX, lastX), Math.min(startY, lastY),
        		  Math.abs(lastX - startX), Math.abs(lastY - startY));
      }
      else
      {
    	  g2.drawRect(Math.min(startX, lastX), Math.min(startY, lastY),
        		  Math.abs(lastX - startX), Math.abs(lastY - startY));
      }
   }

   void draw(Graphics g, int x, int y, int x2, int y2)
   {
	   Graphics2D g2 = (Graphics2D)g;
		  g2.setStroke(new BasicStroke(Thickness, BasicStroke.CAP_ROUND, 0));
	      g2.setColor(color);
	      if(fillFlag == true)
	      {
	    	  g2.fillRect(Math.min(x, x2), Math.min(y,y2),
	        		  Math.abs(x2 - x), Math.abs(y2 - y));
	      }
	      else
	      {
	    	  g2.drawRect(Math.min(x, x2), Math.min(y, y2),
	        		  Math.abs(x2 - x), Math.abs(y2 - y));
	      }
   }
}

@SuppressWarnings("serial")
public class MyJFrame extends JFrame implements Runnable
{
   Thread AutoSaveThread;
   int x,y;
   int startX;
   int startY;
   int lastX;
   int lastY;
   Container contentPane;
   JButton fgButton;
   JButton bgButton;
   MyCanvas canvas;
   Shape currentShape;
   JLabel lblState;
   String selected;
   Vector<Shape> shapeList;

   
   Vector<Shape> RedoList;   
   int Linesize;
   boolean FillSelected;
   boolean DeleteSelected;
   boolean MoveSelected;
   boolean ResizeSelected;
   @SuppressWarnings({ "rawtypes", "unchecked" })
public MyJFrame(){
	  // TODO Auto-generated constructor stub   
	  selected = "Line";
      x = y = 0;
      shapeList = new Vector<Shape>();
      RedoList = new Vector<Shape>();
      
    
      FillSelected = false;		//채우기 선택
      DeleteSelected = false;	//삭제 선택
      MoveSelected = false;		//이동 선택
      ResizeSelected = false;   //리사이즈 선택
      Linesize = 1;
      AutoSaveThread = new Thread(this);
      setTitle("그래픽 에디터");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      contentPane = getContentPane();

      JMenuBar MenuBar = new JMenuBar();
      setJMenuBar(MenuBar);
      
      JMenu mnFile = new JMenu("File");
      JMenuItem mntmSave = new JMenuItem("Save");
      mntmSave.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		try {
				String FileName = JOptionPane.showInputDialog(getContentPane(),"저장할 파일명을 입력하세요.",
						"그림 저장 다이얼로그",JOptionPane.QUESTION_MESSAGE);
				if(FileName == null)
					return;
				ObjectOutputStream SaveFile = new ObjectOutputStream(new FileOutputStream(FileName));
				//배경화면 저장
				SaveFile.writeObject(canvas.getBackground());
				//그림 파일 도형 저장
				for(int i=0;i<shapeList.size();i++)
					SaveFile.writeObject(shapeList.get(i));
			
				
				SaveFile.close();
      			
			} catch (IOException e2) {
				// TODO: handle exception
				System.out.println("입출력 에러 발생!! \n");
				System.exit(1);
			}
      	}
      });

      JMenuItem mntmLoad = new JMenuItem("Load");
      mntmLoad.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent arg0) {
      		//모든 그림 도형 초기화(새로불러오기 위해서 초기화함)
      		shapeList.removeAllElements();
      		//파일선택 다이얼로그 불러오기
      		JFileChooser FileOpenManager = new JFileChooser("C:\\Users\\Administrator\\Desktop");
      		FileOpenManager.showDialog(getContentPane(),"그림 불러오기");
      		File file = FileOpenManager.getSelectedFile();
      		try {
				
      			ObjectInputStream LoadFile = new ObjectInputStream(new FileInputStream(file));
      			//배경화면 불러오기
      			Color color = (Color)LoadFile.readObject();
      			canvas.setBackground(color);
      			//그림 도형 불러오기
      			Shape shape = (Shape)LoadFile.readObject();
      			while(shape != null)
      			{
      				shapeList.add(shape);
      				shape = (Shape)LoadFile.readObject();
      				canvas.repaint();
      			}
      			LoadFile.close();
			} catch (Exception e) {
				// TODO: handle exception

				
			}
      		
      	}
      });
      
      MenuBar.add(mnFile);
      
      mnFile.add(mntmSave);
      mnFile.add(mntmLoad);
      
      JMenuItem mntmQuit = new JMenuItem("Quit");
      mntmQuit.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		System.exit(0);	//프로그램 종료
      	}
      });
      
      mnFile.add(mntmQuit);
      JMenu mnEdit = new JMenu("Edit");
      MenuBar.add(mnEdit);
      JMenuItem mntmLine = new JMenuItem("Line");
      mntmLine.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		selected = "Line";
      	}
      });
      mnEdit.add(mntmLine);
      JMenuItem mntmOval = new JMenuItem("Oval");
      mntmOval.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		selected = "Oval";
      	}
      });
      mnEdit.add(mntmOval);
      JMenuItem mntmRect = new JMenuItem("Rect");
      mntmRect.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		selected = "Rect";
      	}
      });
      mnEdit.add(mntmRect);
      JMenu mnHelp = new JMenu("Help");
      MenuBar.add(mnHelp);
      JMenuItem mntmAbout = new JMenuItem("About");
      mntmAbout.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		JOptionPane.showMessageDialog(getContentPane(), "프로그램 이름 : 그래픽 에디터\r\n 제작자 : 김민성 \r\n",
      				"프로그램 정보",JOptionPane.INFORMATION_MESSAGE);
      	}
      });
      mnHelp.add(mntmAbout);
      contentPane = new JPanel();
      contentPane.setLayout(new BorderLayout(0, 0));
      setContentPane(contentPane);

      canvas = new MyCanvas();	//캔버스 화면
      contentPane.add(canvas, "Center");
      CanvasMouseListener listener = new CanvasMouseListener();
      canvas.addMouseListener(listener);
      canvas.addMouseMotionListener(listener);
      
      canvas.setForeground(Color.black);
      canvas.setBackground(Color.white);
      
      lblState = new JLabel();
      contentPane.add(lblState, "South");
      stateDisplay();
      
      AutoSaveThread.start();

      //create ToolBar
      JToolBar toolBar = new JToolBar();
      contentPane.add(toolBar, "North"); 
      JButton btnLine = new JButton("Line");
      btnLine.setToolTipText("\uC9C1\uC120 \uADF8\uB9AC\uAE30");
      btnLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				selected = b.getText();  //버튼의 텍스트를 얻어온다.
			}
		});
      toolBar.add(btnLine);
      JButton btnOval = new JButton("Oval");
      btnOval.setToolTipText("\uC6D0 \uADF8\uB9AC\uAE30");
		btnOval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				selected = b.getText();
			}
		});
      toolBar.add(btnOval);
      JButton btnRect = new JButton("Rect");
      btnRect.setToolTipText("\uC0AC\uAC01\uD615 \uADF8\uB9AC\uAE30");
        btnRect.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		JButton b = (JButton)e.getSource();
      		selected = b.getText();
      	}
      });
      toolBar.add(btnRect);
      
      //선의 굵기 선택
      JLabel lblNewLabel = new JLabel("    \uD06C\uAE30 ");
      lblNewLabel.setBackground(Color.BLACK);
      lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
      toolBar.add(lblNewLabel);
      Integer [] LineSize = {1,2,3,4,5,6,7,8,9,10};
      JComboBox comboBox = new JComboBox(LineSize);
      comboBox.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		JComboBox combo = (JComboBox)e.getSource();
      		
      		int index = combo.getSelectedIndex();
      		Linesize = index + 1;
      		
      	}
      });
      comboBox.setMaximumRowCount(10);
      toolBar.add(comboBox);
      
      JSeparator separator_2 = new JSeparator();
      separator_2.setOrientation(SwingConstants.VERTICAL);
      toolBar.add(separator_2);
      
      //Undo 버튼
      ImageIcon undoicon = new ImageIcon("images/Undo.jpg");
      JButton UndoButton = new JButton(undoicon);
      UndoButton.setToolTipText("Undo");
      UndoButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		if(!shapeList.isEmpty())
      		{
      			Shape shape = shapeList.get(shapeList.size()-1);
      			shapeList.remove(shapeList.size()-1);
      			RedoList.add(shape);
      			canvas.repaint();
      		}
      	}
      });
      
      ImageIcon recenticon = new ImageIcon("images/recentLoad.jpg");
      JButton recentButton = new JButton(recenticon);
      recentButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent arg0) {
      			try {	
      			ObjectInputStream LoadFile = new ObjectInputStream(new FileInputStream("autoSave"));
      			Color color = (Color)LoadFile.readObject();
      			canvas.setBackground(color);
      			Shape shape = (Shape)LoadFile.readObject();
      			while(shape != null)
      			{
      				shapeList.add(shape);
      				shape = (Shape)LoadFile.readObject();
      				canvas.repaint();
      			}
      			LoadFile.close();
			} catch (Exception e) {
				// TODO: handle exception

				
			}
      		
      	}
      });
      recentButton.setToolTipText("\uCD5C\uADFC \uC791\uC5C5 \uD30C\uC77C \uBD88\uB7EC\uC624\uAE30");
      toolBar.add(recentButton);
      toolBar.add(UndoButton);
      
      //Redo 버튼
      ImageIcon redoicon = new ImageIcon("images/Redo.jpg");
      JButton RedoButton = new JButton(redoicon);
      RedoButton.setToolTipText("Redo");
      RedoButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		if(!RedoList.isEmpty())
      		{
      			Shape shape = RedoList.get(RedoList.size() -1);
      			RedoList.remove(RedoList.size() -1);
      			shapeList.add(shape);
      			canvas.repaint();
      		}
      		
      	}
      });
      toolBar.add(RedoButton);
      
      //채우기 버튼
      ImageIcon fillicon = new ImageIcon("images/Fill.jpg");
      JButton FillButton = new JButton(fillicon);
      FillButton.setToolTipText("\uCC44\uC6B0\uAE30");
      FillButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		selected="";
      		FillSelected = true;
      	}
      });
      
      //새로 그리기 버튼
      ImageIcon newicon = new ImageIcon("images/New.jpg");
      JButton btnNewButton = new JButton(newicon);
      btnNewButton.setToolTipText("\uC0C8\uB85C \uADF8\uB9AC\uAE30");
      btnNewButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent arg0) {
      		selected="";
      		shapeList.removeAllElements();
      		canvas.repaint();
      	}
      });
      toolBar.add(btnNewButton);
      
      JSeparator separator_3 = new JSeparator();
      separator_3.setOrientation(SwingConstants.VERTICAL);
      toolBar.add(separator_3);
      toolBar.add(FillButton);
      
      //도형 이동 버튼
      ImageIcon grepicon = new ImageIcon("images/Grep.jpg");
      JButton GrepButton = new JButton(grepicon);
      GrepButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		MoveSelected = true;
      		selected="";
      	}
      });
      GrepButton.setToolTipText("\uB3C4\uD615 \uC774\uB3D9");
      toolBar.add(GrepButton);
      
      //삭제 버튼
      ImageIcon deleteicon = new ImageIcon("images/Delete.jpg");
      JButton DeleteButton = new JButton(deleteicon);
      DeleteButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		DeleteSelected = true;
      		selected="";
      	}
      });
      DeleteButton.setToolTipText("\uC0AD\uC81C");
      toolBar.add(DeleteButton);
      
      //리사이즈 버튼
      ImageIcon resizeicon = new ImageIcon("images/Resize.jpg");
      JButton ResizeButton = new JButton(resizeicon);
      ResizeButton.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		ResizeSelected = true;
      		selected="";
      	}
      });
      ResizeButton.setToolTipText("\uD06C\uAE30 \uC870\uC808");
      toolBar.add(ResizeButton);
      
      JSeparator separator = new JSeparator();
      separator.setOrientation(SwingConstants.VERTICAL);
      toolBar.add(separator);
      
      
      JLabel lblForeground = new JLabel(" ForeGround ");
      toolBar.add(lblForeground);
      fgButton = new JButton("       ");
      fgButton.addActionListener(new ActionListener() {
      	@Override
      	public void actionPerformed(ActionEvent e) {
			JButton b = (JButton)e.getSource();
			//색상 다이얼로그 불러오기
			Color c = JColorChooser.showDialog(b, "Foreground 선택 창", canvas.getForeground());
		    fgButton.setBackground(c);	//버튼색 변경
		    canvas.setForeground(c);	//캔버스 전경색(그리는 펜의 색깔) 변경
      	}
      });
      fgButton.setBackground(canvas.getForeground());
      toolBar.add(fgButton);
      JLabel lblBackground = new JLabel(" BackGround ");
      lblBackground.setVerticalAlignment(SwingConstants.BOTTOM);
      toolBar.add(lblBackground);
      bgButton = new JButton("       ");
      bgButton.addActionListener(new ActionListener() {
      	@Override
      	public void actionPerformed(ActionEvent e) {
			JButton b = (JButton)e.getSource();
			//색상 다이얼로그 불러오기
			Color c = JColorChooser.showDialog(b, "Background 선택 창", canvas.getBackground());
		    bgButton.setBackground(c);	//버튼색 변경
		    canvas.setBackground(c);	//캔버스 배경색 변경
      	}
      });
      bgButton.setBackground(canvas.getBackground());
      toolBar.add(bgButton);
      JSeparator separator_1 = new JSeparator();
      separator_1.setOrientation(SwingConstants.VERTICAL);
      toolBar.add(separator_1);

      setSize(900,600);
      setVisible(true);
   }
   
   class CanvasMouseListener implements MouseListener, MouseMotionListener{
	  Shape shape = null;
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}
      
      public void mousePressed(MouseEvent e) {
    	  if(MoveSelected == true)
    	  {
    		  for(int i=0;i<shapeList.size();i++)
     		  {
     			  shape = shapeList.get(i);
     			  if(shape.startX <= e.getX() && e.getX() <= shape.lastX &&
     			 	shape.startY <= e.getY() && e.getY() <= shape.lastY )
     			  { 
     	    		    break;
     		  	  }
     	  	  }
    		  Graphics g = ((Canvas) e.getSource()).getGraphics();
 	         
 	          Graphics2D g2 = (Graphics2D)g;
    		  g2.setXORMode(Color.lightGray);
 	          shape.draw(g2);    		  
    	  }
    	  else if(ResizeSelected == true)
    	  {
    		  for(int i=0;i<shapeList.size();i++)
     		  {
     			  shape = shapeList.get(i);
     			  if(shape.startX <= e.getX() && e.getX() <= shape.lastX &&
     			 	shape.startY <= e.getY() && e.getY() <= shape.lastY )
     			  { 
     	    		    break;
     		  	  }
     	  	  }    		  
    	  }
    	  else
    	  {
	         currentShape = Shape.create(selected);
	         //마우스가 클릭된 순간,selected 값에 따라 객체 생성(라인,타원,사각형)후 반환
	         currentShape.color = canvas.getForeground();
	         currentShape.Thickness = Linesize;
	
	         x=startX = lastX = e.getX();
	         y=startY = lastY = e.getY();
	         
	         Graphics g = ((Canvas) e.getSource()).getGraphics();
	         
	         Graphics2D g2 = (Graphics2D)g;
	    	 g2.setStroke(new BasicStroke( currentShape.Thickness, BasicStroke.CAP_ROUND, 0));
	         
	         g2.setXORMode(Color.lightGray);
	         currentShape.draw(g2, startX, startY, lastX, lastY);
    	  }
    	  stateDisplay();

      }
      public void mouseReleased(MouseEvent e) {
    	 if(MoveSelected == true)
    	 {
    		 MoveSelected = false;
    		 canvas.repaint();
    	 }
    	 else if(ResizeSelected == true)
    	 {
    		 ResizeSelected = false;
    		 canvas.repaint();
    	 }
    	 else
    	 {
	         Graphics g = ((Canvas) e.getSource()).getGraphics();
	         
	         Graphics2D g2 = (Graphics2D)g;
	    	 g2.setStroke(new BasicStroke( currentShape.Thickness, BasicStroke.CAP_ROUND, 0));
	         
	         g2.setColor(canvas.getForeground());
	         
	         x = lastX = e.getX();
	         y = lastY = e.getY();
	         
	         currentShape.draw(g2, startX, startY, lastX, lastY);
	         currentShape.setCoordinate(startX, startY, lastX, lastY);
	         shapeList.add(currentShape);
    	 }
      }
      public void mouseClicked(MouseEvent e) {
    	 Shape shape;
    	 if(FillSelected == true)
    	 {
    		 for(int i=0;i<shapeList.size();i++)
    		 {
    			 shape = shapeList.get(i);
    			 if(shape.startX <= e.getX() && e.getX() <= shape.lastX &&
    				shape.startY <= e.getY() && e.getY() <= shape.lastY )
    			 { 
    				shape.color =  canvas.getForeground();
    	    		shape.fillFlag = true;
    	    		canvas.repaint();
    	    		break;
    		  	}
    	  	}
    	 }
    	 else if(DeleteSelected == true)
    	 {
    		 for(int i=0;i<shapeList.size();i++)
    		 {
    			 shape = shapeList.get(i);
    			 if(shape.startX <= e.getX() && e.getX() <= shape.lastX &&
    				shape.startY <= e.getY() && e.getY() <= shape.lastY )
    			 { 
    				shapeList.remove(i);
    				canvas.repaint();
    		  	}
    	  	}
    		 
    	 }
    	 DeleteSelected = false;
    	 FillSelected = false;
      }
      public void mouseDragged(MouseEvent e) 
      {
    	 if(MoveSelected == true)
    	 {
	         x = e.getX();
	         y = e.getY();
	         stateDisplay();
	
	         Graphics g = ((Canvas) e.getSource()).getGraphics();
	         
	         Graphics2D g2 = (Graphics2D)g;
	    	 g2.setStroke(new BasicStroke( shape.Thickness, BasicStroke.CAP_ROUND, 0));
	         
	         g2.setXORMode(Color.lightGray);
	         int width = (shape.lastX - shape.startX)/2;
	         int height = (shape.lastY - shape.startY)/2;         
	         
	         shape.draw(g2, shape.startX,  shape.startY,  shape.lastX,  shape.lastY);
	         shape.draw(g2, x - width,  y - height,  x + width,  y + height);
	       
	         shape.startX = x - width;
	         shape.startY = y - height;
	         shape.lastX = x + width;
	         shape.lastY = y + height;
	         canvas.repaint();
    	 }
    	 else if(ResizeSelected == true)
    	 {
    		 x = e.getX();
	         y = e.getY();
	         stateDisplay();
	         
	         Graphics g = ((Canvas) e.getSource()).getGraphics();
	         
	         Graphics2D g2 = (Graphics2D)g;
	    	 g2.setStroke(new BasicStroke( shape.Thickness, BasicStroke.CAP_ROUND, 0));
	         
	         g2.setXORMode(Color.lightGray);
	         int width = (shape.lastX - shape.startX)/2;
	         int height = (shape.lastY - shape.startY)/2;
	         
	         shape.draw(g2, shape.startX,  shape.startY,  shape.lastX,  shape.lastY);
	         shape.draw(g2, shape.startX,  shape.startY,  x + width,  y + height);
	         shape.lastX = x + width;
	         shape.lastY = y + height;
	         canvas.repaint();
    	 }
    	 else
    	 {
	         x = e.getX();
	         y = e.getY();
	         stateDisplay();
	
	         Graphics g = ((Canvas) e.getSource()).getGraphics();
	         
	         Graphics2D g2 = (Graphics2D)g;
	    	 g2.setStroke(new BasicStroke( currentShape.Thickness, BasicStroke.CAP_ROUND, 0));
	         
	         g2.setXORMode(Color.lightGray);
	         
	         currentShape.draw(g2, startX, startY, lastX, lastY);
	         currentShape.draw(g2, startX, startY, x, y);
	         
	         lastX = x;
	         lastY = y;
    	 }
      }
      
      public void mouseMoved(MouseEvent e) {
         lastX = x = e.getX();
         lastY = y = e.getY();
         stateDisplay();
      }
   }//EndCanvasMouseListener
   
   public void stateDisplay() {
	   
	   String ShapeName = null;
	   if(selected.equals("Line"))
		   ShapeName = "직선";
	   else if(selected.equals("Oval"))
		   ShapeName = "원";
	   else if(selected.equals("Rect"))
		   ShapeName = "사각형";
	   else
		   ShapeName = "선택하지않음";
	   
	      lblState.setText("현재 도형 : " + ShapeName + "      현재좌표 : ( X : " + x + " Y : " + y +")  " + 
	    		  "  펜의 색깔(RGB) : " + canvas.getForeground().toString() +
	    		  "  배경 색깔(RGB) : " + canvas.getBackground().toString());
   }//EndstateDisplay

   class MyCanvas extends Canvas
   {
      public void paint(Graphics g) 
      {
         super.paint(g);
         for(int i = 0; i < shapeList.size(); i++)
         {

            Shape s = (Shape)shapeList.get(i);
            s.draw(g);
         }
      }
   }//EndMyCanvas
   
   public static void main(String[] args) {
      new MyJFrame();
   }

   	@SuppressWarnings("static-access")
	@Override
	public void run() {
	// TODO Auto-generated method stub
   		while(true)
		{
	   		try {
	   			AutoSaveThread.sleep(60000);
	   			//1분 마다 자동 저장한다.
	   			ObjectOutputStream AutoSaveFile = new ObjectOutputStream(new FileOutputStream("autoSave"));
	   			AutoSaveFile.writeObject(canvas.getBackground());
	   			for(int i=0;i<shapeList.size();i++)
	   				AutoSaveFile.writeObject(shapeList.get(i));
	   			AutoSaveFile.close();
			} catch (InterruptedException e) {
				return;
				// TODO: handle exception
			} catch (IOException e2) {
				return;
			}
	   		
   		}
	}
}