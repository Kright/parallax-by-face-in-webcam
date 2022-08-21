package com.kright

import org.openimaj.image.processing.face.detection.DetectedFace
import math.{IVector2d, Vector2d}


class ParallaxCalculator(imageSize: Vector2d):
  private val previous = new Vector2d(0, 0)

  var scale: Double = 300.0
  var mixFactor: Double = 0.5 // 0.0 - no mix
  var faceSize: Double = 1.0

  def apply(faces: java.util.List[DetectedFace]): IVector2d =
    if (!faces.isEmpty) {
      val face = faces.get(0)
      val (currentCenter, currentFaceSize) = getNew(face)

      previous *= mixFactor
      previous.madd(currentCenter, 1.0 - mixFactor)

      faceSize = (faceSize * mixFactor) + currentFaceSize * (1.0 - mixFactor)
    }
    previous

  def getNew(face: DetectedFace): (Vector2d, Double) =
    val rect = face.getBounds
    val currentFaceSize = scala.math.sqrt(rect.height * rect.width) / imageSize.x
    val distance = 1.0 / faceSize

    val rectCenter = Vector2d(rect.x + rect.width / 2, rect.y + rect.height / 2)
    val imageCenter = imageSize / 2
    val relativeRectCenter = rectCenter - imageCenter

    relativeRectCenter *= (scale * distance / imageSize.x)
    relativeRectCenter.x *= -1
    (relativeRectCenter, currentFaceSize)
