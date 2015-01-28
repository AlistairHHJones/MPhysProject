import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import odeint
import math


def f(x,t,params):
    
    G,l2,N,n,hs,hd,sigma = params

    #End point
    derivs = []
    derivs.append(0.0)
    
    #Left side of chain
    for i in range(1,N/2-1):
        t = G*(l**2)*hs*(x[i+1] + x[i-1] - 2*x[i] + 2*sigma*(N-1)*(x[i+1] + x[i-1] - 1)*x[i]*(1-x[i]) )
        derivs.append( t )

    #Boundary speakers
    t1 = G*(l**2)*( hd*(x[N/2] - x[N/2-1]) + hs*( x[N/2-2] - x[N/2-1] ) + sigma*(N-1)*( 2*( hd*x[N/2] + hs*x[N/2-2]) - (hs + hd))*x[N/2-1]*(1-x[N/2-1]) )
    derivs.append( t1 )

    t2 = G*(l**2)*( hs*(x[N/2+1] - x[N/2]) + hd*( x[N/2-1] - x[N/2] ) + sigma*(N-1)*( 2*( hs*x[N/2+1] + hd*x[N/2-1]) - (hs + hd))*x[N/2]*(1-x[N/2]) )
    derivs.append( t2 )

    #Right right of chain
    for i in range(N/2+1,N-1):
        t3 = G*(l**2)*hs*(x[i+1] + x[i-1] - 2*x[i] + 2*sigma*(N-1)*(x[i+1] + x[i-1] - 1)*x[i]*(1-x[i]) )
        derivs.append( t3 )

    #End point
    derivs.append(0.0)

    return derivs


N = 10
n = N/2
hs = 0.1
hd = hs/10
sigma = 0.00

G = 1.0/N
l = 0.01
l2 = l**2

numSteps = pow(10,7)

params = [G,l2,N,n,hs,hd,0.00]
params1 = [G,l2,N,n,hs,hd,0.1]
params2 = [G,l2,N,n,hs,hd,0.01]
params3 = [G,l2,N,n,hs,hd,0.001]

x0 = []
x0.append(0.0)
for i in range(1,N-1):
    x0.append(0.5)
x0.append(1.0)

t = np.arange(0., numSteps, 1.0)

psoln = odeint(f,x0,t,args=(params,))
psoln1 = odeint(f,x0,t,args=(params1,))
psoln2 = odeint(f,x0,t,args=(params2,))
psoln3 = odeint(f,x0,t,args=(params3,))

#fig = plt.figure(1, figsize=(8,8))
#ax = fig.add_subplot(313)
#ax.plot(psoln[:,1])
#ax.set_xlabel('xi')
#ax.set_ylabel('Prob')
plt.plot(psoln[numSteps-1,:], label = '0.0')
plt.plot(psoln1[numSteps-1,:], label = '0.1')
plt.plot(psoln2[numSteps-1,:], label = '0.01')
plt.plot(psoln3[numSteps-1,:], label = '0.001')
plt.legend(loc = 2)
plt.show()



