import math
from ODEs import LinearODE as f
import numpy as np
import scipy.sparse.linalg as sp

np.set_printoptions(threshold='nan')

def LinearJacobian(x,f,params):

    #Unpack parameters
    l2,N,n,hs,hd,sigma = params
    G = 1.0/N

    A = []

    #Rows of jacobian
    for i in range (0,N):
        Ai = []

        #Columns of jacobian
        for j in range (0,N):

            if 0 < i < n-1 or n < i < N-1:
                if j == i+1 or j == i-1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0-x[i])) )
                elif i == j:
                    Ai.append( hs*(4.0*sigma*(x[i+1] + x[i-1] - 1.0)*(1.0 - 2.0*x[i]) - 2.0 ))
                else:
                    Ai.append(0.0)

            if i == n:
                if j == i+1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif j == i-1:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hs*x[i+1] + hd*x[i-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

            if i == 0:
                if j == i+1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 -x[i]) ) )
                elif j == N-1:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 -x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hs*x[i+1] + hd*x[N-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

            if i == N - 1:
                if j == 0:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif j == i-1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 -x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hd*x[0] + hs*x[i-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

            if i == n-1:
                if j == i+1:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif j == i-1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hd*x[i+1] + hs*x[i-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

        A.append(Ai)

    A = np.asarray(A)
    return A

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
    x.append(0.9)
for i in range(n,N):
    x.append(0.1)

#Jacobian
A = LinearJacobian(x,f,params)

#Find eigenvalues and eigenvectors
eVals, eVecs = sp.eigs(A,98)

#Count number of positive and negative eigenvalues
positive = 0
negative = 0
for i in range (0,98):
    if eVals[i] > 0.0:
        positive = positive + 1
    else:
        negative = negative + 1

print positive, negative

print eVals
