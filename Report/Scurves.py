import matplotlib.pylab as plt

age = [85,75,65,55,45,35,25,17]
couch =	[0.06, 0.095, 0.205, 0.235, 0.488, 0.655, 0.789, 0.851]
chesterfield = [0.726,0.667,0.692,0.546,0.307,0.164,0.063,0.06]
sofa = [0.131,0.19,0.026,0.218,0.199,0.181,0.135,0.06]
davenport = [0.012,0.024,0,0,0,0,0.006,0]
settee = [0.024,0,0.077,0,0.006,0,0.003,0]

plt.xlim([85,17])
plt.plot(age,couch,label="Couch")
plt.plot(age,chesterfield,label="Chesterfield")
plt.plot(age,sofa,label="Sofa")
plt.plot(age,davenport,label="Davenport")
plt.plot(age,settee,label="Settee")

plt.legend(loc=5)
plt.xlabel("Speaker age group (years)")
plt.ylabel("Variant frequency")
plt.show()
