from scipy.optimize import fsolve
import math
    
N = 50
n = N/2
sigma = 0.0
hs = 0.01
hd = hs
    
a = n*hd
b = (N-1)*sigma*(n-1)*hs
c = (N-1)*sigma*n*hd

def equations(p):
    x, y = p
    
    f1 = y*( c*(1.-y) + a + b*(2*y**2 - 3*y + 1.) )/( a + 2*c*y*(1.-y) ) - x
    f2 = x*( c*(1.-x) + a + b*(2*x**2 - 3*x + 1.) )/( a + 2*c*x*(1.-x) ) - y
    return (f1,f2)

x, y =  fsolve(equations, (0.0,0.0))

print equations((x, y))