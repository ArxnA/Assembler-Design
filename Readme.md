# Assembler Design Project - Phase 1 and phase 2

This repository contains the implementation of my course project, which consists of two phases. The first phase focuses on designing an assembler, while the second phase involves memory visualization for a custom microcontroller. Together, these phases demonstrate the fundamentals of assembly language processing, machine code generation, and memory management.

---

## Phase 1: Assembler Design

The assembler converts low-level assembly language instructions into machine language (binary format). It supports a range of operations, including arithmetic, logic, stack, and control flow commands.

## Features

### Supported Instructions

- **Arithmetic and Logic Operations**: `ADD`, `SUB`, `AND`, `OR`, `XOR`, `INC`, `DEC`
- **Stack Operations**: `PUSH`, `POP`
- **Control Flow**: `JMP` (short jump, both forward and backward)

### Implementation Details

- **Register-to-Register Operations**: Handles transfers and operations exclusively between registers, adhering to `MOD 11` settings.
- **Memory Addressing**: Supports simple register addressing without displacement or SIB (e.g., `ADD eax, [ebx]`).
- **Short Jump Implementation**: Implements forward and backward short jump instructions (e.g., `JMP Label`).

## Input and Output

- **Input**: A file containing assembly instructions.
- **Output**: A file containing the corresponding memory addresses and machine code in little-endian format.

### Example

**Input File:**

```
ADD eax, ecx
SUB al, bl
```

**Output File:**

```
0x0000000000000000: 01 C8    (ADD eax, ecx)
0x0000000000000002: 28 D8    (SUB al, bl)
```

## Project Breakdown

### Section 1: Register Operations

In this section, the assembler handles transfers and operations between registers. For example:

```
ADD eax, ecx <======> 01 c8
```

Key considerations:

- Supports all register families.
- Adheres to `MOD 11` settings.

### Section 2: Memory Operations

Incorporates memory addressing into operations. For example:

```
ADD eax, [ebx] <======> 03 03
SUB [ecx], edx <======> 29 11
```

This section ensures correct `MOD` settings for mixed operations between registers and memory.

### Section 3: Jump Instructions

Implements short jumps, both forward and backward. For example:

```
ADD eax, ecx <======> 01 c8
I_am_here: <======> nothing
ADD bx, dx <======> 66 01 d3
JMP I_am_here <===> eb fb
```

## Resources

- [x86 Assembly Basics](http://www.c-jump.com/CIS77/CPU/x86/lecture.html#X77_0210)
- [Online Assembler and Disassembler](http://shell-storm.org/online/Online-Assembler-and-Disassembler/)

## Notes

- The assembler is designed for a little-endian architecture.
- All machine codes are based on x86 instruction encoding standards.

## How to Run

1. Provide a file with valid assembly instructions as input.
2. Run the assembler program.
3. Check the output file for the translated machine code.

---

## Phase 2: Memory Visualization

## Introduction
This part is designed to visualize the memory layout of a custom microcontroller, X1313, which has a memory capacity of 256 bytes and a width of 8 bits. The memory is divided into three distinct segments:

- **Code Segment**: Stores machine instructions.
- **Data Segment**: Stores initialized variables.
- **Stack Segment**: Used for function calls and temporary data storage.

This part takes an input file defining the contents of these segments and generates a memory visualization adhering to specific alignment and filling rules.

## Features
- Parses input files containing `.stack`, `.data`, and `.code` definitions.
- Visualizes memory addressing and contents.
- Handles word alignment for stack and code segments.
- Supports different data types (BYTE, WORD, DWORD) for the data segment.
- Fills unused memory appropriately with `MM` (within segments) or `XX` (outside segments).
- Implements even alignment for machine instructions in the code segment.

## Memory Segmentation
1. **Code Segment**: 
   - Starts at a specified address.
   - Fills with machine codes generated in part 1 of the project.
   - Ensures even alignment for all instructions.

2. **Data Segment**: 
   - Starts at a specified address.
   - Fills with data values of different sizes (BYTE, WORD, DWORD).

3. **Stack Segment**: 
   - Starts at a specified address.
   - Fills with register names or numeric values based on push operations.
   - Unused stack space is filled with `MM`.

4. **Unused Memory**: 
   - Any memory not allocated to a segment is filled with `XX`.

## Input Format
The input is a text file containing three sections: `.stack`, `.data`, and `.code`. Each section specifies its starting address and the corresponding content. For example:

```
.stack(200)

.data(100)
myByte BYTE
myWord WORD
myDword DWORD

.code(0)
INC ax
```

### Input Rules
- Segment starting addresses are even.
- Segment lengths are fixed at 32 bytes each.
- Memory width is 8 bits.
- Logical and physical addresses are identical.

## Output Format
The output is a memory visualization table showing:
- Memory addresses.
- Content of each byte.
- Segment boundaries.



## Memory Filling Rules
- **Stack Segment**: 
  - Aligns to 32-bit boundaries.
  - Fills with register names, numbers, or `MM` for unused bytes.

- **Data Segment**: 
  - Fills based on the size of declared variables (BYTE, WORD, DWORD).

- **Code Segment**: 
  - Fills with machine codes.
  - Aligns instructions to even addresses.

- **Unused Memory**:
  - `MM` for unused bytes in allocated segments.
  - `XX` for bytes outside all segments.


## Example Input and Output
### Input:
```
.stack(200)

.data(100)
myByte BYTE
myWord WORD

.code(0)
INC ax
```

### Output:
```
Address  Content
0x00     40
0x01     MM
...      ...
0x64     myByte
0x65     myWord
0x66     myWord

```

## How to Run
1. Prepare an input file following the format described above.
2. Run the program with the input file as an argument.
3. View the memory visualization output in the console or a generated file.

## About

This project was completed as part of a computer engineering course assignment. It demonstrates the fundamentals of assembly language processing and machine code generation.

Feel free to explore the code and provide feedback or suggestions!
