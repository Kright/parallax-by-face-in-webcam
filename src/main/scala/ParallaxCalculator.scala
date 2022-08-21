package com.kright

import org.openimaj.image.processing.face.detection.DetectedFace

import math.Vector2d


class ParallaxCalculator(imageSize: Vector2d):
  var scale = 300.0
  val previous = new Vector2d(0, 0)
  var mixFactor = 0.5 // 0.0 - no mix

  def apply(faces: java.util.List[DetectedFace]): Vector2d =
    if (!faces.isEmpty) {
      val face = faces.get(0)
      previous *= mixFactor
      previous.madd(getNew(face), 1.0 - mixFactor)
    }
    previous

  def getNew(face: DetectedFace): Vector2d =
    val rect = face.getBounds
    val faceSize = scala.math.sqrt(rect.height * rect.width) / imageSize.x
    val distance = 1.0 / faceSize

    val rectCenter = Vector2d(rect.x + rect.width / 2, rect.y + rect.height / 2)
    val imageCenter = imageSize / 2
    val relativeRectCenter = rectCenter - imageCenter

    relativeRectCenter *= (scale * distance / imageSize.x)
    relativeRectCenter.x *= -1
    relativeRectCenter
