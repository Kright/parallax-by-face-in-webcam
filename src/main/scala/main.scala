package com.kright

import com.github.sarxos.webcam.Webcam

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamPanel
import com.github.sarxos.webcam.WebcamResolution
import org.openimaj.image.ImageUtilities

import java.awt.event.{KeyEvent, KeyListener}
import java.awt.{Canvas, Color, Dimension, Graphics}
import javax.swing.{JFrame, JPanel}
import javax.swing.WindowConstants.{DISPOSE_ON_CLOSE, EXIT_ON_CLOSE}
import javax.imageio.ImageIO
import java.io.File
import org.openimaj.image.processing.face.detection.DetectedFace
import org.openimaj.image.processing.face.detection.HaarCascadeDetector
import math.Vector2d


@main
def main(): Unit = {
  //  makePanel()
  processImagesAndShow()
}

def processImagesAndShow(): Unit = {
  val imageSize = Dimension(176, 144)
  val jpanelSize = Dimension(1680, 1024)

  val webcam = Webcam.getDefault()
  webcam.setViewSize(imageSize)
  webcam.open(true)

  val backgroundImage = ImageIO.read(new File("images/city_v2.jpeg"))
  val detector = new HaarCascadeDetector()

  val parallaxCalculator = new ParallaxCalculator(Vector2d(imageSize.width, imageSize.height))

  val jpanel = new JPanel() {
    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      val image = webcam.getImage()
      val faces = detector.detectFaces(ImageUtilities.createFImage(image))
      val parallax = parallaxCalculator(faces)

      g.drawImage(
        backgroundImage,
        parallax.x.toInt - backgroundImage.getWidth() / 2 + jpanelSize.width / 2,
        parallax.y.toInt - backgroundImage.getHeight() / 2 + jpanelSize.height / 2,
        null
      )

      g.drawImage(image, 0, 0, null)
      g.setColor(Color.RED)
      faces.forEach{ face =>
        val rect = face.getBounds
        g.drawRect(rect.x.toInt, rect.y.toInt, rect.width.toInt, rect.height.toInt)
      }

      g.drawString(s"scale = ${parallaxCalculator.scale.toInt}", 0, g.getFontMetrics.getHeight)
      this.repaint()
    }
  }

  jpanel.setSize(jpanelSize)
  jpanel.setPreferredSize(jpanelSize)
  jpanel.setVisible(true)
  jpanel.setFocusable(false)

  val window = new JFrame("parallax by webcam")

  window.addKeyListener(new MyKeyListener(webcam, parallaxCalculator))

  window.add(jpanel)
  window.setResizable(true)
  window.setDefaultCloseOperation(EXIT_ON_CLOSE)
  window.pack()
  window.setVisible(true)

  while (window.isEnabled) {
    Thread.sleep(1)
  }
}

class MyKeyListener(webcam: Webcam, parallaxCalculator: ParallaxCalculator) extends KeyListener {
  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_F2 =>
        val image = webcam.getImage()
        val path = s"imgs/${System.currentTimeMillis()}.png"
        image.savePng(path)
        println(s"image saved as ${path}")
      case KeyEvent.VK_EQUALS =>
        parallaxCalculator.scale *= 1.1
      case KeyEvent.VK_MINUS =>
        parallaxCalculator.scale /= 1.1
      case KeyEvent.VK_0 =>
        parallaxCalculator.mixFactor = 0.0
      case KeyEvent.VK_9 =>
        parallaxCalculator.mixFactor = 0.5
      case KeyEvent.VK_8 =>
        parallaxCalculator.mixFactor = 0.8

  override def keyReleased(e: KeyEvent): Unit = {}
}

def makePanel(): Unit = {
  val webcam: Webcam = Webcam.getDefault
  //  webcam.setViewSize(WebcamResolution.VGA.getSize)

  val panel: WebcamPanel = new WebcamPanel(webcam)
  panel.setFPSDisplayed(true)
  panel.setDisplayDebugInfo(true)
  panel.setImageSizeDisplayed(true)
  panel.setMirrored(true)

  val window: JFrame = new JFrame("Test webcam panel")
  window.add(panel)
  window.setResizable(true)
  window.setDefaultCloseOperation(EXIT_ON_CLOSE)
  window.pack()

  window.setVisible(true)
}

extension (image: BufferedImage)
  def savePng(path: String): Unit =
    ImageIO.write(image, "PNG", new File(path))