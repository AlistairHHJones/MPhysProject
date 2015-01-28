import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import odeint
from ODEs import TwoGroupODE
import math

# System parameters
N = 10
n = N/2
hs = 0.1
hd = hs/10
sigma = 0.
l = 0.01

# Set lambda squared to be 1
l2 = 1.0

params = [l2,N,n,hs,hd,sigma]

# Length of time and step size
length = pow(10.0,9)
step = length/2

# Initial values
x0 = []
for i in range(0,N/2):
    x0.append(1.0)
for i in range(N/2,N):
    x0.append(0.0)

t = np.arange(0., length, step)

# Solve odes
psoln = odeint(TwoGroupODE,x0,t,args=(params,))

# Plot results
plt.plot(psoln[1,:], label = '0.1')

plt.legend(loc = 2)
plt.show()
