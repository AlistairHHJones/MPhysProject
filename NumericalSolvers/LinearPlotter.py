import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import odeint
from ODEs import LinearODE
import math

# System parameters
N = 10
n = N/2
hs = 0.1
hd = hs/10
sigma = 0.00
l = 0.01

# Set lambda squared to be 1
l2 = 1.0

params = [l2,N,n,hs,hd,0.00]
params1 = [l2,N,n,hs,hd,0.1]
params2 = [l2,N,n,hs,hd,0.05]
params3 = [l2,N,n,hs,hd,0.001]

# Length of time and step size
length = pow(10.0,8)
step = length/2

# Initial values
x0 = []
x0.append(0.0)
for i in range(1,N-1):
    x0.append(0.5)
x0.append(1.0)

t = np.arange(0., length, step)

# Solve odes
psoln = odeint(linearODE,x0,t,args=(params,))
psoln1 = odeint(linearODE,x0,t,args=(params1,))
psoln2 = odeint(linearODE,x0,t,args=(params2,))
psoln3 = odeint(linearODE,x0,t,args=(params3,))

# Plot results
plt.plot(psoln1[1,:], label = '0.1')
plt.plot(psoln2[1,:], label = '0.01')
plt.plot(psoln3[1,:], label = '0.001')
plt.plot(psoln[1,:], label = '0.0')
plt.legend(loc = 2)
plt.show()
