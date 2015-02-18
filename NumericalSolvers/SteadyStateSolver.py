import math
import numpy as np
import matplotlib.pyplot as plt
import scipy.sparse.linalg as sp
import numpy.linalg as linalg
from ODEs import LinearJacobian
from ODEs import TwoGroupJacobian

np.set_printoptions(threshold='nan')
#plt.ylim([-1,1])

#File
#file = open("+ve_-ve_sigma.txt", "w")
positiveArray = []
sigmaArray = []

#for i in range(1,1001):

# System parameters
N = 100
n = N/2
hs = 0.1
hd = hs/10
sigma = 0.01
l = 0.01

# Set lambda squared to be 1
l2 = 1.0

params = [l2,N,n,hs,hd,sigma]

# Equilibrium values
x = []
for i in range(0,n):
    x.append(0.5)
for i in range(n,N):
    x.append(0.5)

#Jacobian
A = LinearJacobian(x,params)
#A = TwoGroupJacobian(x,params)
#print(A)

#Find eigenvalues and eigenvectors
eVals, eVecs = sp.eigs(A,98)

#Count number of positive and negative eigenvalues
positive = 0
negative = 0
iRange = len(eVals)
unstable = []
for i in range (0,iRange):
    if eVals[i] > 0.0:
        positive = positive + 1
        unstable.append(i)
    else:
        negative = negative + 1


positiveArray.append(positive)
sigmaArray.append(sigma)
print positive
#data = positive,negative,sigma
#file.write(str(positive) + " " + str(negative) + " " +  str(sigma) + "\n" )

#file.close()
# Plot results
for i in range(0,positive):
    plt.plot(eVecs[:,unstable[i]])

#plt.legend(loc = 2)
plt.show()

#print eVals
