import chisel3._

/**
  * Emit SystemVerilog for the AES module.
  *
  * Usage examples:
  *   sbt "runMain AESEmit --key-size 128 --target-dir generated"
  */
object AESEmit extends App {
  def get(name: String, default: String): String = {
    val idx = args.indexOf(s"--$name")
    if (idx >= 0 && idx + 1 < args.length) args(idx + 1) else default
  }

  val keySize   = get("key-size", "128").toInt
  val targetDir = get("target-dir", "generated")

  val stageArgs = Array("-td", targetDir)
  val verilog = chisel3.emitVerilog(new AES(keySize), stageArgs)
  println(s"Verilog emitted to '$targetDir' (top: AES.sv). Length=${verilog.length}")
}

