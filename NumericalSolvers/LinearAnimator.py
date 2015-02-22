import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
from scipy.integrate import odeint
from ODEs import LinearODE

fig, ax = plt.subplots()
ax.set_ylim([0,1])

# System parameters
N = 100
n = N/2
hs = 1
hd = hs
sigma = 0.01
l = 0.01

# Set lambda squared to be 1
l2 = 1.0

params = [l2,N,n,hs,hd,sigma]

# Length of time and step size
length = pow(10.0,6)
step = length/1000

t = np.arange(1, length, step)

# Initial values
x0 = []
for i in range(0,N):
    if i == 25:
        x0.append(0.51)
    elif i == 75:
        x0.append(0.49)
    else:
        x0.append(0.5)


# Solve odes
psoln = odeint(LinearODE,x0,t,args=(params,))

# Line to plot
line, = ax.plot(psoln[i,:])

# Function to animate
def animate(i):

    line.set_ydata(psoln[i,:])
    return line

# Clean frame
def init():

    line.set_ydata(x0)
    return line

# Animate
anim = animation.FuncAnimation(fig, animate, np.arange(1,1000), init_func=init,
    interval=25, blit=False)


#anim.save('LinearChain.mp4', fps=30, extra_args=[])

plt.show()
