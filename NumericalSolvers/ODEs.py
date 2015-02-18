import math
import numpy as np

#Coupled odes for two group system
def TwoGroupODE(x,t,params):

    l2,N,n,hs,hd,sigma = params
    G = 2.0/(N*(N-1))

    # Derivative functions
    derivs = []

    # Sum over speakers
    for i in range(0,N):
        dxibydt = 0.0
        hij = 0.0
        si = 0.0
        siStar = sigma*( (n-1)*hs + n*hd )

        for j in range(0,N):
            if (i != j):
                if (i < n and j < n) or (i > n-1 and j > n-1):
                    hij = hs
                else:
                    hij = hd
                si = si + sigma*hij*x[j]

        for j in range (0,N):
            if (i != j):
                if (i < n and j < n) or (i > n-1 and j > n-1):
                    hij = hs
                else:
                    hij = hd

                dxibydt = dxibydt + G*l2*( hij*( x[j] - x[i] ) + ( 2*si - siStar )*x[i]*(1-x[i]) )

        derivs.append( dxibydt )

    return derivs


#Jacobian matrix for 2 group
def TwoGroupJacobian(x,params):

    l2,N,n,hs,hd,sigma = params
    G = 2.0/(N*(N-1))

    # Jacobian
    A = []

    # Interactor weights
    h = []
    for i in range(0,N):
        hi = []
        for j in range(0,N):
            if i != j:
                if (i < n and j < n) or (i > n-1 and j > n-1):
                    hi.append(hs)
                else:
                    hi.append(hd)
            else:
                hi.append(0.0)

        h.append(hi)

    # Build jacobian
    for i in range(0,N):
        Ai = []
        for k in range(0,N):

            # Calulate sum_jneqk term
            sum_jneqk = 0.0
            sk = 0.0
            skStar = sigma*( (n-1)*hs + n*hd )
            for j in range(0,N):
                if j != k:
                    sk = sk + sigma*h[k][j]*x[j]

            for j in range(0,N):
                sum_jneqk = sum_jneqk + (2*sk - skStar)*(1-2*x[k]) - h[k][j]

            Ai.append( G*l2*( h[i][k] + 2*(N-1)*sigma*h[i][k]*x[i]*(1-x[i]) + sum_jneqk) )

        A.append(Ai)

    A = np.asarray(A)
    return A


#Coupled odes for linear chain
def LinearODE(x,t,params):

    l2,N,n,hs,hd,sigma = params
    G = 1.0/N

    # End point
    derivs = []
    #derivs.append(0.0)
    #derivs.append(G*(l2)*hs*(2*x[1] - 2*x[0] + 2*sigma*2*(2*x[1] - 1)*x[0]*(1-x[0])))
    derivs.append(G*(l2)*( hs*(x[1] - x[0]) + hd*( x[N-1] - x[0] ) + sigma*2*( 2*( hs*x[1] + hd*x[N-1]) - (hs + hd))*x[0]*(1-x[0])))

    # Left side of chain
    for i in range(1,N/2-1):
        t = G*(l2)*hs*(x[i+1] + x[i-1] - 2*x[i] + 2*sigma*2*(x[i+1] + x[i-1] - 1)*x[i]*(1-x[i]) )
        derivs.append( t )

    # Boundary speakers
    t1 = G*(l2)*( hd*(x[N/2] - x[N/2-1]) + hs*( x[N/2-2] - x[N/2-1] ) + sigma*2*( 2*( hd*x[N/2] + hs*x[N/2-2]) - (hs + hd))*x[N/2-1]*(1-x[N/2-1]) )
    derivs.append( t1 )

    t2 = G*(l2)*( hs*(x[N/2+1] - x[N/2]) + hd*( x[N/2-1] - x[N/2] ) + sigma*2*( 2*( hs*x[N/2+1] + hd*x[N/2-1]) - (hs + hd))*x[N/2]*(1-x[N/2]) )
    derivs.append( t2 )

    # Right right of chain
    for i in range(N/2+1,N-1):
        t3 = G*(l2)*hs*(x[i+1] + x[i-1] - 2*x[i] + 2*sigma*2*(x[i+1] + x[i-1] - 1)*x[i]*(1-x[i]) )
        derivs.append( t3 )

    # End point
    #derivs.append(0.0)
    #derivs.append(G*(l2)*hs*(2*x[N-2] - 2*x[N-1] + 2*sigma*2*(2*x[N-2] - 1)*x[N-1]*(1-x[N-1])))
    derivs.append(G*(l2)*( hd*(x[0] - x[N-1]) + hs*( x[N-2] - x[N-1] ) + sigma*2*( 2*( hd*x[0] + hs*x[N-2]) - (hs + hd))*x[N-1]*(1-x[N-1]) ))

    return derivs


#Linear jacobian matrix
def LinearJacobian(x,params):

    #Unpack parameters
    l2,N,n,hs,hd,sigma = params
    G = 1.0/N

    A = []

    #Rows of jacobian
    for i in range (0,N):
        Ai = []

        #Columns of jacobian
        for j in range (0,N):

            if 0 < i < n-1 or n < i < N-1:
                if j == i+1 or j == i-1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0-x[i])) )
                elif i == j:
                    Ai.append( hs*(4.0*sigma*(x[i+1] + x[i-1] - 1.0)*(1.0 - 2.0*x[i]) - 2.0 ))
                else:
                    Ai.append(0.0)

            if i == n:
                if j == i+1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif j == i-1:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hs*x[i+1] + hd*x[i-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

            if i == 0:
                if j == i+1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 -x[i]) ) )
                elif j == N-1:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 -x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hs*x[i+1] + hd*x[N-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

            if i == N - 1:
                if j == 0:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif j == i-1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 -x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hd*x[0] + hs*x[i-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

            if i == n-1:
                if j == i+1:
                    Ai.append( hd*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif j == i-1:
                    Ai.append( hs*(1.0 + 4.0*sigma*x[i]*(1.0 - x[i]) ) )
                elif i == j:
                    Ai.append( 2.0*sigma*( 2.0*(hd*x[i+1] + hs*x[i-1]) - (hs + hd))*(1.0 - 2.0*x[i]) - (hs + hd) )
                else:
                    Ai.append(0.0)

        A.append(Ai)

    A = np.asarray(A)
    return A
