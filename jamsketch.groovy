import controlP5.ControlP5
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import processing.core.PImage
import processing.core.PApplet;
import Particle;


// added by yonamine 20230208
import java.io.File
import java.io.FileReader
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.processing.gui.SimplePianoRoll




class JamSketch extends SimplePianoRoll {


  GuideData guideData
  MelodyData2 melodyData

  String username = ""
  int fullMeasure
  int mCurrentMeasure
  double DebugModeDraw

  static def CFG
  PImage backgroundImage;

  ArrayList<Particle> particles;
  boolean drawParticles = false;
  boolean isMousePressed = false;
  boolean nowDrawing = false

  void setup() {
    super.setup()
    size(1200, 700)
    particles = new ArrayList<>();
    // Load the background image
    backgroundImage = loadImage("C:/Users/asano/JamSketchJunior/images/GrandPiano.jpg");

    showMidiOutChooser()
    def p5ctrl = new ControlP5(this)
    p5ctrl.addButton("startMusic").
    setLabel("Start / Stop").setPosition(20, 645).
      setSize(120, 40)
    p5ctrl.addButton("resetMusic").
    setLabel("Reset").setPosition(160, 645).setSize(120, 40)
    

      p5ctrl.addButton("loadCurve").
      setLabel("Load").setPosition(300, 645).setSize(120, 40)


    if (CFG.MOTION_CONTROLLER != null) {
      CFG.MOTION_CONTROLLER.each { mCtrl ->
        JamSketch.main("JamSketchSlave", [mCtrl] as String[])
      }
    }

    initData()
    // add WindowListener (windowClosing) which calls exit();
  }

  void initData() {
    melodyData = new MelodyData2(CFG.MIDFILENAME, (width - CFG.getKeyboardWidth) as int, this, this, CFG)
    smfread(melodyData.scc.getMIDISequence())
    def part =
      melodyData.scc.getFirstPartWithChannel(CFG.CHANNEL_ACC)
    setDataModel(
      part.getPianoRollDataModel(
	    CFG.INITIAL_BLANK_MEASURES,
            CFG.INITIAL_BLANK_MEASURES + CFG.NUM_OF_MEASURES
      ))
    if (CFG.SHOW_GUIDE)
      guideData = new GuideData(CFG.MIDFILENAME, (width - CFG.getKeyboardWidth) as int, this)
    fullMeasure = dataModel.getMeasureNum() * CFG.REPEAT_TIMES;
  }


  void draw() {
    super.draw()

    // Tomb added BG and transparency
    tint(255, 200);
    image(backgroundImage, 0, 0, width, height);
    noTint();
    if (isMousePressed) {
      // Create particles only if drawParticles is true
      if (drawParticles) {
        // Create a new particle and add it to the list
        particles.add(new Particle(mouseX, mouseY, (float) (5 + Math.random() * (15 - 5)), 100)); // 100 frames lifespan
      }

      // Update and display particles
      for (int i = particles.size() - 1; i >= 0; i--) {
        Particle p = particles.get(i);
        p.update();
        p.display(this);  // Pass the sketch to the display method
        // Remove dead particles from the list
        if (p.isDead()) {
          particles.remove(i);
        }
      }
    }

    if (guideData != null)
      drawGuideCurve()
    if (CFG.FORCED_PROGRESS) {
      mouseX = beat2x(getCurrentMeasure() + CFG.HOW_IN_ADVANCE, getCurrentBeat());
    }

    if(pmouseX < mouseX &&
            mouseX > beat2x(getCurrentMeasure(), getCurrentBeat()) + 10) {
      if (isUpdatable()) {
        storeCursorPosition()
        updateCurve()
      }
    }
    drawCurve()
    if (getCurrentMeasure() == CFG.NUM_OF_MEASURES - CFG.NUM_OF_RESET_AHEAD)
      processLastMeasure()
    melodyData.engine.setFirstMeasure(getDataModel().
      getFirstMeasure())
    enhanceCursor()
    drawProgress()
  }

  void drawCurve() {
    strokeWeight(3)
    stroke(0, 0, 255)

    int currentMeasure = getCurrentMeasure();

    for (int i = 0; i < melodyData.curve1.size() - 1; i++) {
      if (melodyData.curve1[i] != null && melodyData.curve1[i + 1] != null) {
        if ((currentMeasure + CFG.NUM_OF_RESET_AHEAD) % 8 != 0) {
          // Draw a line between two points only if it's not the 8th measure
          line(i + CFG.getKeyboardWidth, melodyData.curve1[i] as int, i + CFG.getKeyboardWidth + 1,
                  melodyData.curve1[i + 1] as int);
        }

        // Draw particles at each point
        drawParticles(i + CFG.getKeyboardWidth, melodyData.curve1[i] as int);
      }
    }
  }


  void drawParticles(float x, float y) {
    // Draw particles only if drawParticles is true
    // Draw particles only when the mouse is pressed
    if (drawParticles) {
      // Create a new particle and add it to the list
      Particle p = new Particle(x, y, (float) (10 + Math.random() * (30 - 10)), 50); // Adjust size and lifespan
//      p.display(this);  // Display the particle once

    }
  }


  void drawGuideCurve() {
    def xFrom = 100
    strokeWeight(3)
    stroke(100, 200, 200)
    (0..<(guideData.curveGuideView.size()-1)).each { i ->
      if (guideData.curveGuideView[i] != null &&
      guideData.curveGuideView[i+1] != null) {
        line(i+xFrom, guideData.curveGuideView[i] as int,
             i+1+xFrom, guideData.curveGuideView[i+1] as int)
      }
    }
  }

  void updateCurve() {
    println("updateCurve called")
    melodyData.updateCurve(pmouseX, mouseX)
    // Add the following line to reset the curve
    melodyData.resetCurve()
  }

  void storeCursorPosition() {
    (pmouseX..mouseX).each { i ->

      melodyData.curve1[i-CFG.getKeyboardWidth] = mouseY
      println("Y axis ${mouseY}")
      println("X axis ${mouseX}")
    }
  }

  boolean isUpdatable() {
    if ((!CFG.ON_DRAG_ONLY || nowDrawing) &&
            isInside(mouseX, mouseY)) {
      int m1 = x2measure(mouseX)
      int m0 = x2measure(pmouseX)
      0 <= m0 && pmouseX < mouseX
    } else {
      false
    }
  }

  void processLastMeasure() {
    makeLog("melody")
    if (CFG.MELODY_RESETTING) {
      if (mCurrentMeasure < (fullMeasure - CFG.NUM_OF_RESET_AHEAD)) getDataModel().shiftMeasure(CFG.NUM_OF_MEASURES)
      melodyData.resetCurve()
      if (guideData != null) guideData.shiftCurve()
    }
  }

  void enhanceCursor() {
    if (CFG.CURSOR_ENHANCED) {
      fill(255, 0, 0)
      ellipse(mouseX, mouseY, 10, 10)
    }
  }

  void drawProgress() {
    if (isNowPlaying()) {
      def dataModel = getDataModel()
      mCurrentMeasure = getCurrentMeasure() +
              dataModel.getFirstMeasure() -
              CFG.INITIAL_BLANK_MEASURES + 1
      int mtotal = dataModel.getMeasureNum() *
                   CFG.REPEAT_TIMES
      textSize(32)
      fill(0, 0, 0)
      text(mCurrentMeasure + " / " + mtotal, 460, 675)
    }
  }
  
  void stop() {
    super.stop()
    //featext.stop()
  }

  void startMusic() {
    if (isNowPlaying()) {
      stopMusic()
      makeLog("stop")
    } else {
      playMusic()
      makeLog("play")
    }
  }

  void resetMusic() {
    initData()
    setTickPosition(0)
    dataModel.setFirstMeasure(CFG.INITIAL_BLANK_MEASURES)
    makeLog("reset")
  }

  @Override
  void musicStopped() {
    super.musicStopped()
//    if (microsecondPosition >= sequencer.getMicrosecondLength())
//      resetMusic()
  }



  void makeLog(action) {
    def logname = "output_" + (new Date()).toString().replace(" ", "_").replace(":", "-")
    if (action == "melody") {
      def midname = "${CFG.LOG_DIR}/${logname}_melody.mid"
      melodyData.scc.toWrapper().toMIDIXML().writefileAsSMF(midname)
//      println("saved as ${midname}")
      def sccname = "${CFG.LOG_DIR}/${logname}_melody.sccxml"
      melodyData.scc.toWrapper().writefile(sccname)
//      println("saved as ${sccname}")
      def jsonname = "${CFG.LOG_DIR}/${logname}_curve.json"
      saveStrings(jsonname, [JsonOutput.toJson(melodyData.curve1)] as String[])
//      println("saved as ${jsonname}")
      def pngname = "${CFG.LOG_DIR}/${logname}_screenshot.png"
      save(pngname)
//      println("saved as ${pngname}")
      // for debug
      new File("${CFG.LOG_DIR}/${logname}_noteList.txt").text = (melodyData.scc as SCCDataSet).getFirstPartWithChannel(1).getNoteList().toString()
//      new File("${CFG.LOG_DIR}/${logname}_noteOnlyList.txt").text = (melodyData.scc as SCCDataSet).getFirstPartWithChannel(1).getNoteOnlyList().toString()

    } else {
      def txtname = "${CFG.LOG_DIR}/${logname}_${action}.txt"
      saveStrings(txtname, [action] as String[])
//      println("saved as ${txtname}")
    }
  }

  void loadCurve() {
    selectInput("Select a file to process:", "loadFileSelected")
  }

  void loadFileSelected(File selection) {
    if (selection == null) {
      println("Window was closed or the user hit cancel.")
    } else {
      def absolutePath = selection.getAbsolutePath()
      println("User selected " + absolutePath)
      if (absolutePath.endsWith(".json")) {
        def json = new JsonSlurper()

        melodyData.curve1 = json.parseText(selection.text)
        int count = melodyData.curve1.count(null)

        melodyData.updateCurve(0, width - (DebugModeDraw as int))
      } else if (selection.getCanonicalPath().endsWith(".txt")) {
        println("Reading ${absolutePath}")
        def table = loadTable(absolutePath, "csv")
        melodyData.curve1 = [null] * (width - (DebugModeDraw as int))
        int n = table.getRowCount()
        int m = melodyData.curve1.size()
        for (int i in (getKeyboardWidth() as int)..<(melodyData.curve1.size() - 1)) {
          int from = (i - (getKeyboardWidth() as int)) * n / m
          int thru = ((i + 1) - (getKeyboardWidth() as int)) * n / m - 1
          melodyData.curve1[i] =
                  (from..thru).collect { notenum2y(table.getFloat(it, 0)) }.sum() /
                          (from..thru).size()
        }
        melodyData.updateCurve(0,  width - (DebugModeDraw as int))
      }else {
        println("File is not supported")
        return
      }
    }
  }

  void mousePressed() {
    nowDrawing = true
    isMousePressed = true;
    drawParticles = true;
  }

  void mouseReleased() {
    isMousePressed = false;
    nowDrawing = false;
    particles.clear();

    if (isInside(mouseX, mouseY)) {
      println("Inside isInside condition");
      if (!melodyData.engine.automaticUpdate()) {
        println("!melodyData.engine.automaticUpdate() is true");
        melodyData.engine.outlineUpdated(
                x2measure(mouseX) % CFG.NUM_OF_MEASURES,
                CFG.DIVISION - 1
        );
        // Add a direct call to updateCurve here
        updateCurve();
      } else {
        println("!melodyData.engine.automaticUpdate() is false");
      }
    } else {
      println("Outside isInside condition");
    }
  }



  void mouseDragged() {

  }

  void keyReleased() {
    if (key == ' ') {
      if (isNowPlaying()) {
      	stopMusic()
      } else {
        setTickPosition(0)
        getDataModel().setFirstMeasure(CFG.INITIAL_BLANK_MEASURES)
        playMusic()
      }
    } else if (key == 'b') {
      setNoteVisible(!isNoteVisible());
      println("Visible=${isVisible()}")
   } else if (key == 'u') {
     melodyData.updateCurve('all')
    }

}
  void LoadOutlineLayerData() {

    def jsonFile = new File(CFG.INPUT_FILE_PATH)
    def jsonSlurper = new JsonSlurper()
    def jsonData  = jsonSlurper.parse(jsonFile)
    jsonData.eachWithIndex{v, i-> 

      melodyData.curve1[i] = v

    }

    melodyData.updateCurve(0, (width - getKeyboardWidth()) as int)
    // processLastMeasure()
  }

  public void exit() {
    println("exit() called.")
    super.exit()
    if (CFG.MOTION_CONTROLLER.any{mCtrl == "RfcommServer"}) RfcommServer.close()
  }

//  static void main(String[] args) {
//     JamSketch.CFG = evaluate(new File("./config.txt"))
////    JamSketch.CFG = evaluate(new File("./config_guided.txt"))
//    JamSketch.start("JamSketch")
//  }

}
//JamSketch.CFG = evaluate(new File("./config.txt"))
JamSketch.CFG = evaluate(new File("./config_guided.txt"))
JamSketch.start("JamSketch")
// JamSketch.main("JamSketch", ["--external"] as String[])
  
