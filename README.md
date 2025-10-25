# AES (Chisel) — 128/192/256

A parameterizable AES-128/192/256 encryption core written in Chisel. Implements the standard AES round functions and a generic key expansion (FIPS-197). The implementation is based on NIST documentations (https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.197-upd1.pdf)

- Block/input size: 128 bits
- Key sizes: 128, 192, 256 bits
- Interface: simple start/done handshake, 128-bit input/output

## Project Layout
- `build.sbt`
- `src/main/scala/AES.scala` — AES core
- `src/test/scala/` — unit tests 

## Requirements
- JDK 11+ and sbt 1.8+
- Scala 2.13.x
- Chisel 6.7.0, chiseltest 6.0.0 (managed in `build.sbt`)

## Quick Start
From the project directory:

```bash
cd aes-chisel
sbt test                       # run all tests
sbt "testOnly AESTest"     # run a specific test
```

Emit the SystemVerilog file:

```bash
sbt "runMain AESEmit --key-size 128 --target-dir generated"
```

## Module Interface
`AES(key_size: Int)` where `key_size ∈ {128, 192, 256}`

- `in_data: UInt(128.W)` — plaintext input block
- `in_key: UInt(key_size.W)` — cipher key
- `start: Bool` — pulse high for one cycle to begin
- `done: Bool` — high when output is valid
- `out: UInt(128.W)` — ciphertext output block

Behavior:
- On `start := true.B`, the core expands the key, runs AES rounds, then raises `done` with `out` valid.
- `done` remains high in the final state until `start` is low again, then the FSM returns to idle.

## Implementation Notes
- Round functions: SubBytes (S-box), ShiftRows, MixColumns, AddRoundKey
- Final round omits MixColumns
- Key expansion:
  - `Nk = key_size / 32` (4, 6, 8)
  - `Nr = Nk + 6` (10, 12, 14)
  - Seeds first `Nk` words from `in_key` (MSB-first)
  - Expands to `4 × (Nr + 1)` words using RotWord/SubWord/Rcon rules
- FSM: `sIdle` → `sRound0` → `sRoundCore` × (Nr − 1) → `sFinalRound` → `sDone`

## Testing
Tests use ScalaTest + chiseltest and live under `src/test/scala`.

Examples:
```bash
sbt test
sbt "testOnly AESTest"
```

## Roadmap / Limitations
- Encryption only (no decryption/inverse rounds yet)
- Single-block, non-streaming interface
- Not pipelined; timing/area optimizations left for future work
- Add golden-vector tests for 192/256-bit keys

## License
Created by RifkiFi

