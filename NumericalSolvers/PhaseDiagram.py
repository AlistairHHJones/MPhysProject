import matplotlib.pyplot as plt
import math


x = [0.006,0.002,0.001,0.001,0.001,0.0008,0.0007,0.0005,0.0005,0.0005]
y = [10,20,30,40,50,60,70,80,90,100]

graph = [x,y]

plt.ylim([0,100])
plt.plot(graph[0], graph[1])
plt.show()
