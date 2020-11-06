'''
    @author: nxi
'''

from gumpy.nexus import array as nxa
from errorhandler import *

class ndarray():
    def __init__(self, shape = None, dtype=float, buffer=None, offset=None, strides=None, order=None):
        if buffer is None:
            self.buffer = nxa.instance(shape, dtype = dtype)
        else:
            if isinstance(buffer, ndarray):
                buffer = buffer.buffer
            if strides is None:
                self.buffer = buffer
            else:
                if offset is None:
                    offset = [0] * buffer.ndim
                self.buffer = buffer.get_section(offset, shape, strides)
    
    '''
    T : ndarray
        The transposed array.

    data: buffer
        Python buffer object pointing to the start of the array's data.

    dtype : dtype object
        Data-type of the array's elements.

    flags : not supported
        Information about the memory layout of the array.

    flat : numpy.flatiter object
        A 1-D iterator over the array.

    imag : not supported
        The imaginary part of the array.

    real : not supported
        The real part of the array.

    size : int
        Number of elements in the array.

    itemsize : int
        Length of one array element in bytes.

    nbytes : int
        Total bytes consumed by the elements of the array.

    ndim : int
        Number of array dimensions.

    shape : tuple of ints
        Tuple of array dimensions.

    strides : tuple of ints
        Tuple of bytes to step in each dimension when traversing an array.

    ctypes : not supported

    base : ndarray
        Base object if memory is from some other object.

    '''
    def __getattr__(self, name):
        if name == 'shape':
            return tuple(self.buffer.shape)
        elif name == 'data':
            return self.buffer
        elif name == 'T':
            t = self.buffer.transpose()
            return self._new(buffer = t)
        elif name == 'dtype':
            return self.buffer.dtype
        elif name == 'flags':
            raise NotSupportedError('flags is not supported in Gumpy')
        elif name == 'flat':
            return ndarray(buffer = self.buffer.flatten())
        elif name == 'imag':
            raise NotSupportedError('complex data type is not supported in Gumpy')
        elif name == 'real':
            raise NotSupportedError('complex data type is not supported in Gumpy')
        elif name == 'ndim':
            return self.buffer.ndim
        elif name == 'size':
            return self.buffer.size
        elif name == 'itemsize':
            return self.buffer.itemsize
        elif name == 'nbytes':
            return self.buffer.nbytes
        elif name == 'strides':
            return self.buffer.stride
        elif name == 'ctype':
            raise NotSupportedError('C type is not supported in Gumpy')
        elif name == 'base':
            raise NotSupportedError('memory access is not supported in Gumpy')
        else:
            raise AttributeError('attribute {} not found'.format(name))
        
    def __getitem__(self, index):
        out = self.buffer.__getitem__(index)
        if isinstance(out, nxa.Array):
            return ndarray(None, buffer = out)
        else:
            return out
        
    def __setitem__(self, index, value):
        self.buffer.__setitem__(index, value)
        
    def __str__(self, indent = ''):
        return self.buffer.__str__(indent)
    
    def __repr__(self, indent = ''):
        out = self.buffer.__repr__(indent)
        return 'a' + out[1:]
        
    def __len__(self):
        return self.shape[0]
    
    ############## logic functions ##############
    
    def __eq__(self, obj):
        if isinstance(obj, ndarray):
            return ndarray(buffer = self.buffer == obj.buffer)
        else :
            return ndarray(buffer = self.buffer == obj) 
    
    ############## math functions ###############
    def __ne__(self, obj):
        if isinstance(obj, ndarray):
            return ndarray(buffer = self.buffer != obj.buffer)
        else :
            return ndarray(buffer = self.buffer != obj)
    
    def _new(self, buffer):
        return ndarray(buffer = buffer)
        
    def item(self, *args):
        return self.buffer.item(*args)
        
    def copy(self, subok=True):
        if subok:
            return self._new(buffer = self.buffer.__copy__())
        else:
            return ndarray(buffer = self.buffer.__copy__())
        
    def all(self, axis=None, out=None, keepdims=False):
        res = self.buffer.all(axis)
        if out is None:
            if keepdims:
                return ndarray(self.shape, bool).fill(res)
            else:
                return res
        else:
            out.fill(res)
            return out

    def any(self, axis=None, out=None, keepdims=False):
        res = self.buffer.any(axis)
        if out is None:
            if keepdims:
                return ndarray(self.shape, bool).fill(res)
            else:
                return res
        else:
            out.fill(res)
            return out

    def argmax(self, axis=None, out=None):
        res = self.buffer.argmax(axis)
        if not out is None:
            out.fill(res)
            return out
        return res
        
    def argmin(self, axis=None, out=None):
        res = self.buffer.argmin(axis)
        if not out is None:
            out.fill(res)
            return out
        return res
        
    ''' Copy of the array, cast to a specified type.
    
        Parameters
    
            dtype : str or dtype
                Typecode or data-type to which the array is cast.
    
            order : not supported
    
            casting : not supported
                
            subok : bool, optional
                If True, then sub-classes will be passed-through (default), 
                otherwise the returned array will be forced to be a base-class 
                array.

            copy : not supported
    
        Returns
    
            arr_t : ndarray
                A new array of the same shape as the input array, with dtype, 
                order given by dtype, order.
    
        Raises
    
            ComplexWarning
                When casting from complex to float or int. To avoid this, 
                one should use a.real.astype(t).

    '''
    def astype(self, dtype, order='K', casting='unsafe', subok=True, copy=True):
        if subok:
            return self._new(buffer = self.buffer.astype(dtype))
        else:
            return ndarray(buffer = self.buffer.astype(dtype))
        
    ''' Return a copy of the array collapsed into one dimension.
    
        Parameters
    
            order : not supported
    
        Returns
    
            y : ndarray
                A copy of the input array, flattened to one dimension.
    
    '''
    def flatten(self, order='C'):
        return ndarray(buffer=self.buffer.flatten())
    
    def clip(self, min=None, max=None, out=None):
        if out is None:
            return self._new(buffer = self.buffer.clip(min, max))
        else :
            if not isinstance(out, ndarray):
                out = np.asanyarray(out)
            return self._new(buffer = self.buffer.clip(min, max, out.buffer))
            
        
    def moveaxis(self, source, destination):
        if type(source) is int:
            if destination < 0:
                destination = self.ndim + destination
            dims = range(self.ndim)
            s = dims[source]
            nd = dims[:source] + dims[source + 1:]
            nd.insert(destination, s)
        elif hasattr(source, '__len__'):
            dims = range(self.ndim)
            s = []
            for i in xrange(len(source)):
                s.append([dims[source[i]], destination[i]])
            s = sorted(s, key = lambda g:g[1])
            nd = []
            source.sort()
            start = -1
            for i in source:
                nd += dims[start : i]
                start = i + 1
            nd += dims[start :]
            for g in s:
                nd.insert(g[1], g[0])
        return self._new(buffer=self.buffer.permute(nd))
    
    def swapaxes(self, axis1, axis2):
        nd = range(self.ndim)
        nd[axis1] = axis2
        nd[axis2] = axis1
        return self._new(buffer=self.buffer.permute(nd))
    
    def transpose(self, axes=None):
        if axes != None and len(axes) > 2:
            return self._new(buffer = self.buffer.permute(axes))
        else:
            return self._new(buffer = self.buffer.transpose(axes))
       
    def compress(self, condition, axis=None, out=None):
        if out is None:
            return self._new(buffer = self.buffer.compress(condition, axis))
        else:
            out = np.asanyarray(out)
            return self._new(buffer = self.buffer.compress(condition, axis, out.buffer))
        
    '''
    Fill the array with a scalar value.

    Parameters

       value : scalar
            All elements of a will be assigned this value.
    '''
    def fill(self, value):
        self.buffer.fill(value)
        
    def reshape(self, *shape):
        if len(shape) == 0:
            raise IllegalArgumentError('shape must be provided')
        if len(shape) == 1 :
            if type(shape[0]) is list:
                return self._new(buffer = self.buffer.reshape(shape[0]))
            else:
                return self._new(buffer = self.buffer.reshape(list(shape[0])))
        else:
            return self._new(buffer = self.buffer.reshape(list(shape)))
        
    def squeeze(self, axis = None):
        return ndarray(buffer = self.buffer.squeeze(axis))
        
    def diagonal(self, offset=0, axis1=0, axis2=1):
        return ndarray(buffer = self.buffer.diagonal(offset, axis1, axis2))
    
    def tril(self, k=0):
        return ndarray(buffer = self.buffer.tril(k))

    def triu(self, k=0):
        return ndarray(buffer = self.buffer.triu(k))
        
    ''' Return the array as an a.ndim-levels deep nested list of Python scalars.

        Return a copy of the array data as a (nested) Python list. Data items are 
        converted to the nearest compatible builtin Python type, via the item 
        function.
        
        If a.ndim is 0, then since the depth of the nested list is 0, it will not 
        be a list at all, but a simple Python scalar.
        
        Parameters
        
            none
        
        Returns
        
            y : object, or list of object, or list of list of object, or 
        
                The possibly nested list of array elements.

    '''
    def tolist(self):
        if self.size == 0:
            return []
        else:
            return self.buffer.tolist()
        
    ''' the following methods were not implemented
    
        argpartition(kth[, axis, kind, order])
    

        argsort([axis, kind, order])
    

        astype(dtype[, order, casting, subok, copy])
    

        byteswap([inplace])
        
        choose(choices[, out, mode])
        
        conj()
        
        conjugate()
    
    '''