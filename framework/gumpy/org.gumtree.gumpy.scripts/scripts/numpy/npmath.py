'''
    @author: nxi
'''
import numpy as np

''' Clip (limit) the values in an array.

    Given an interval, values outside the interval are clipped to the 
    interval edges. For example, if an interval of [0, 1] is specified, 
    values smaller than 0 become 0, and values larger than 1 become 1.

    Equivalent to but faster than np.minimum(a_max, np.maximum(a, a_min)).

    No check is performed to ensure a_min < a_max.

    Parameters

        a : array_like
            Array containing elements to clip.

        a_min : scalar or array_like or None
            Minimum value. If None, clipping is not performed on lower 
            interval edge. Not more than one of a_min and a_max may be None.

        a_max : scalar or array_like or None
            Maximum value. If None, clipping is not performed on upper 
            interval edge. Not more than one of a_min and a_max may be None. 
            If a_min or a_max are array_like, then the three arrays will be 
            broadcasted to match their shapes.

        out : ndarray, optional
            The results will be placed in this array. It may be the input 
            array for in-place clipping. out must be of the right shape to 
            hold the output. Its type is preserved.

    Returns

        clipped_array : ndarray
            An array with the elements of a, but where values < a_min are 
            replaced with a_min, and those > a_max with a_max.

'''
def clip(a, a_min, a_max, out=None, **kwargs):
    a = np.asanyarray(a)
    return a.clip(a_min, a_max, out)

''' Return the cumulative product of elements along a given axis.

    Parameters

        a : array_like
            Input array.
            
        axis : int, optional
            Axis along which the cumulative product is computed. 
            By default the input is flattened.

        dtype : dtype, optional
            Type of the returned array, as well as of the accumulator 
            in which the elements are multiplied. If dtype is not 
            specified, it defaults to the dtype of a, unless a has an 
            integer dtype with a precision less than that of the 
            default platform integer. In that case, the default 
            platform integer is used instead.

        out : ndarray, optional
            Alternative output array in which to place the result. It 
            must have the same shape and buffer length as the expected 
            output but the type of the resulting values will be cast 
            if necessary.

    Returns

        cumprod : ndarray

            A new array holding the result is returned unless out is 
            specified, in which case a reference to out is returned.

'''
def cumprod(a, axis=None, dtype=None, out=None):
    a = np.asanyarray(a)
    return a.cumprod(axis, dtype, out)

''' Return the cumulative sum of the elements along a given axis.

    Parameters

        a : array_like
            Input array.
            
        axis : int, optional
            Axis along which the cumulative sum is computed. The default (None) 
            is to compute the cumsum over the flattened array.
        
        dtype : dtype, optional
            Type of the returned array and of the accumulator in which the 
            elements are summed. If dtype is not specified, it defaults to 
            the dtype of a, unless a has an integer dtype with a precision 
            less than that of the default platform integer. In that case, 
            the default platform integer is used.
        
        out : ndarray, optional
            Alternative output array in which to place the result. It must 
            have the same shape and buffer length as the expected output 
            but the type will be cast if necessary. See ufuncs-output-type 
            for more details.

    Returns

        cumsum_along_axis : ndarray.
            A new array holding the result is returned unless out is specified, 
            in which case a reference to out is returned. The result has the 
            same size as a, and the same shape as a if axis is not None or a is 
            a 1-d array.

'''
def cumsum(a, axis=None, dtype=None, out=None):
    a = np.asanyarray(a)
    return a.cumsum(axis, dtype, out)

''' Dot product of two arrays. Specifically,

        If both a and b are 1-D arrays, it is inner product of vectors (without complex conjugation).

        If both a and b are 2-D arrays, it is matrix multiplication, but using matmul or a @ b is preferred.

        If either a or b is 0-D (scalar), it is equivalent to multiply and using numpy.multiply(a, b) or a * b is preferred.

        If a is an N-D array and b is a 1-D array, it is a sum product over the last axis of a and b.

        If a is an N-D array and b is an M-D array (where M>=2), it is a sum product over the last axis of a and the second-to-last axis of b:

        dot(a, b)[i,j,k,m] = sum(a[i,j,:] * b[k,:,m])

    Parameters

        a : array_like
            First argument.

        b : array_like
            Second argument.

        out : ndarray, optional
            Output argument. This must have the exact kind that would be 
            returned if it was not used. In particular, it must have the 
            right type, must be C-contiguous, and its dtype must be the 
            dtype that would be returned for dot(a,b). This is a 
            performance feature. Therefore, if these conditions are not 
            met, an exception is raised, instead of attempting to be 
            flexible.

    Returns

        outputndarray

            Returns the dot product of a and b. If a and b are both scalars 
            or both 1-D arrays then a scalar is returned; otherwise an array 
            is returned. If out is given, then it is returned.

    Raises

        ValueError
            If the last dimension of a is not the same size as the 
            second-to-last dimension of b.

'''
def dot(a, b, out=None):
    if np.iterable(a):
        a = np.asanyarray(a)
        return a.dot(b, out)
    elif np.iterable(b):
        b = np.asanyarray(b)
        return b.dot(a, out)
    else:
        return a * b

''' Return the maximum of an array or maximum along an axis.

    Parameters

        a : array_like
            Input data.

        axis : None or int or tuple of ints, optional
            Axis or axes along which to operate. 
            By default, flattened input is used.

            If this is a tuple of ints, the maximum is selected over multiple axes, 
            instead of a single axis or all the axes as before.

        out : ndarray, optional
            Alternative output array in which to place the result. Must be of the 
            same shape and buffer length as the expected output. See ufuncs-output-type 
            for more details.

        keepdims : not supported
        
        initial : scalar, optional
            The minimum value of an output element. Must be present to allow computation 
            on empty slice. See reduce for details.

        where : not supported

    Returns

        amax : ndarray or scalar
            Maximum of a. If axis is None, the result is a scalar value. If axis is given, 
            the result is an array of dimension a.ndim - 1.

'''
def amax(a, axis=None, out=None, keepdims=None, initial=None, where=None):
    a = np.asanyarray(a)
    return a.max(axis, out, initial = initial)

''' Return the minimum of an array or minimum along an axis.

    Parameters

        a : array_like
            Input data.

        axis : None or int or tuple of ints, optional
            Axis or axes along which to operate. By default, flattened input is used.
            If this is a tuple of ints, the minimum is selected over multiple axes, 
            instead of a single axis or all the axes as before.

        out : ndarray, optional
            Alternative output array in which to place the result. Must be of the same 
            shape and buffer length as the expected output. See ufuncs-output-type for 
            more details.
        
        keepdims : not supported

        initial : scalar, optional
            The maximum value of an output element. Must be present to allow computation 
            on empty slice. See reduce for details.

        where : not supported

    Returns

        amin : ndarray or scalar
            Minimum of a. If axis is None, the result is a scalar value. If axis is given, 
            the result is an array of dimension a.ndim - 1.

'''
def amin(a, axis=None, out=None, keepdims=None, initial=None, where=None):
    a = np.asanyarray(a)
    return a.min(axis, out, initial = initial)

''' Compute the arithmetic mean along the specified axis.

    Returns the average of the array elements. The average is taken over 
    the flattened array by default, otherwise over the specified axis. 
    float64 intermediate and return values are used for integer inputs.

    Parameters

        a : array_like
            Array containing numbers whose mean is desired. If a is not 
            an array, a conversion is attempted.
            
        axis : None or int or tuple of ints, optional
            Axis or axes along which the means are computed. The default 
            is to compute the mean of the flattened array.

            If this is a tuple of ints, a mean is performed over multiple 
            axes, instead of a single axis or all the axes as before.
            
        dtype : data-type, optional
            Type to use in computing the mean. For integer inputs, the 
            default is float64; for floating point inputs, it is the same 
            as the input dtype.

        out : ndarray, optional
            Alternate output array in which to place the result. The 
            default is None; if provided, it must have the same shape as 
            the expected output, but the type will be cast if necessary. \
            
        keepdims : not supported

    Returns

        m : ndarray, see dtype parameter above
            If out=None, returns a new array containing the mean values, 
            otherwise a reference to the output array is returned.

'''
def mean(a, axis=None, dtype=None, out=None, keepdims=None):
    a = np.asanyarray(a)
    return a.mean(axis, dtype, out, keepdims)

''' Return the indices of the elements that are non-zero.

    Returns a tuple of arrays, one for each dimension of a, containing the 
    indices of the non-zero elements in that dimension.
    
    Parameters

        a : array_like
            Input array.

    Returns

        tuple_of_arrays : tuple
            Indices of elements that are non-zero.

'''
def nonzero(a):
    a = np.asanyarray(a)
    return a.nonzero()
