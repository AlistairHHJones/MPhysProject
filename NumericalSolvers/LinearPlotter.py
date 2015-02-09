import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import odeint
from ODEs import LinearODE
import math


plt.ylim([0,1])
# System parameters
N = 100
n = N/2
hs = 0.1
hd = hs/30
sigma = 0.0010
l = 0.01

# Set lambda squared to be 1
l2 = 1.0

params = [l2,N,n,hs,hd,sigma]

# Length of time and step size
length = pow(10.0,8)
step = length/2

t = np.arange(1, length, step)

# Initial values
x0 = []
for i in range(0,N):
    if i == 25:
        x0.append(1.0)
    elif i == 75:
        x0.append(0.0)
    else:
        x0.append(0.5)

# Solve odes
psoln = odeint(LinearODE,x0,t,args=(params,))

# Plot results
plt.plot(psoln[1,:], label = sigma)
plt.legend(loc = 2)
plt.show()
