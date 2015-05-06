# Test Name: testing.txt (iteration 0)
# Generated by TheFuzz v1.0.0 written by Alex Kersten and Kenneth Siu for ECE552 (https://github.com/akersten/TheFuzz)

  SRA R3, R0, 1                  #; (0>>>1=0); Z->1
  SRL R8, R3, 3                  #; (0>>3=0); Z->1
  ADD R4, R8, R0                 #; (0+0=0); Z->1; N->0; V->0
  ADD R1, R0, R3                 #; (0+0=0); Z->1; N->0; V->0
  ADD R7, R3, R8                 #; (0+0=0); Z->1; N->0; V->0
  LHB R10, 121                   #; R10=30976
  SRA R10, R0, 0                 #; (0>>>0=0); Z->1
  SRA R11, R1, 5                 #; (0>>>5=0); Z->1
  SLL R0, R3, 1                  #; (0<<1=0); No change to R0; Z->1
  SLL R7, R11, 3                 #; (0<<3=0); Z->1
  SLL R11, R1, 3                 #; (0<<3=0); Z->1
  ADD R0, R8, R3                 #; (0+0=0); No change to R0; Z->1; N->0; V->0
  SRA R5, R0, 7                  #; (0>>>7=0); Z->1
  SRL R13, R4, 0                 #; (0>>0=0); Z->1
  ADD R5, R1, R10                #; (0+0=0); Z->1; N->0; V->0
  LLB R10, 86                    #; R10=86
  SRL R6, R10, 6                 #; (86>>6=1); Z->0
  SRL R10, R7, 4                 #; (0>>4=0); Z->1
  ADD R9, R13, R11               #; (0+0=0); Z->1; N->0; V->0
  SRL R5, R3, 0                  #; (0>>0=0); Z->1
  HLT                            #; 

# R0 = 0
# R1 = 0
# R2 = xxxx
# R3 = 0
# R4 = 0
# R5 = 0
# R6 = 1
# R7 = 0
# R8 = 0
# R9 = 0
# R10 = 0
# R11 = 0
# R12 = xxxx
# R13 = 0
# R14 = xxxx
# R15 = xxxx

# Z = 1 N = 0 V = 0

# Valid memory addresses: