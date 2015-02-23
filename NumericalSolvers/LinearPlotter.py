import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import odeint
from ODEs import LinearODE
import math

sigmaC = []

for j in range(1,11):
    print j

    fixation = True

    w = 0
    while fixation == True:
        # System parameters
        N = 100
        n = N/2
        hs = 1
        hd = hs/j
        sigma = 0.00001*w
        l = 0.01

        w = w+1
        print w

        # Set lambda squared to be 1
        l2 = 1.0

        params = [l2,N,n,hs,hd,sigma]

        # Length of time and step size
        length = pow(10.0,10)
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


        print abs(psoln[1,25] - psoln[1,75])
        #Evaluate fixation
        if abs(psoln[1,25] - psoln[1,75]) > 0.95:
            sigmaC.append(sigma)
            fixation = False

print sigmaC

# Plot results
plt.plot(sigmaC)
plt.show()
#plt.plot(psoln[1,:], label = sigma)
#plt.legend(loc = 2)
#plt.show()
