import math

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
