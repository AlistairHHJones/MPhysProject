import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import odeint
from ODEs import TwoGroupODE
import math

# System parameters
N = 100
n = N/2
hs = 0.1
hd = hs/70
sigma = 0.01
l = 0.01

# Set lambda squared to be 1
l2 = 1.0

params = [l2,N,n,hs,hd,0.0008]

params1 = [l2,N,n,hs,hd,0.0007]

params2 = [l2,N,n,hs,hd,0.0004]
params3 = [l2,N,n,hs,hd,0.0002]
params4 = [l2,N,n,hs,hd,0.00008]

# Length of time and step size
length = pow(10.0,8)
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
psoln1 = odeint(TwoGroupODE,x0,t,args=(params1,))
psoln2 = odeint(TwoGroupODE,x0,t,args=(params2,))
psoln3 = odeint(TwoGroupODE,x0,t,args=(params3,))
psoln4 = odeint(TwoGroupODE,x0,t,args=(params4,))

# Plot results
plt.plot(psoln[1,:], label = '0.0008')
plt.plot(psoln1[1,:], label = '0.0006')
plt.plot(psoln2[1,:], label = '0.0004')
plt.plot(psoln3[1,:], label = '0.0002')
#plt.plot(psoln4[1,:], label = '0.0005')

plt.legend(loc = 2)
plt.show()
